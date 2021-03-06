package io.horizontalsystems.bitcoincore.transactions

import io.horizontalsystems.bitcoincore.transactions.scripts.ScriptType

class TransactionSizeCalculator {
    val signatureLength = 72 + 1      // signature length + pushByte
    val pubKeyLength = 33 + 1         // pubKey length + pushByte
    private val p2wpkhShLength = 22 + 1       // 0014<20-byte-script-hash> + pushByte

    private val legacyTx = 16 + 4 + 4 + 16    // 40 Version + number of inputs + number of outputs + locktime
    private val legacyWitnessData = 1         // 1 Only 0x00 for legacy input
    private val witnessTx = legacyTx + 1 + 1  // 42 segwit marker + segwit flag
    private val witnessData = 1 + signatureLength + pubKeyLength  // 108 Number of stack items for input + Size of stack item 0 + Stack item 0, signature + Size of stack item 1 + Stack item 1, pubkey

    private val sigLengths = mapOf(
            ScriptType.P2PKH to signatureLength + pubKeyLength,
            ScriptType.P2PK to signatureLength,
            ScriptType.P2WPKHSH to p2wpkhShLength
    )

    private val lockingScriptSizes = mapOf(
            ScriptType.P2PK to 35,
            ScriptType.P2PKH to 25,
            ScriptType.P2SH to 23,
            ScriptType.P2WPKH to 22,
            ScriptType.P2WSH to 34,
            ScriptType.P2WPKHSH to 23
    )

    fun outputSize(scripType: ScriptType): Int {
        return 8 + 1 + getLockingScriptSize(scripType)
    }

    fun inputSize(scriptType: ScriptType): Int {
        val sigLength = sigLengths[scriptType] ?: 0

        return 32 + 4 + 1 + sigLength + 4 // PreviousOutputHex + OutputIndex + sigLength + sigScript + sequence
    }

    fun transactionSize(inputs: List<ScriptType>, outputs: List<ScriptType>, pluginDataOutputSize: Int): Long {
        val txIsWitness = inputs.any { it.isWitness }
        val txWeight = if (txIsWitness) witnessTx else legacyTx

        val inputWeight = inputs.map { inputSize(it) * 4 + if (txIsWitness) witnessSize(it) else 0 }.sum()
        var outputWeight = outputs.map { outputSize(it) }.sum() * 4 // to vbytes

        if (pluginDataOutputSize > 0) {
            outputWeight += outputSizeByScriptSize(pluginDataOutputSize) * 4
        }

        return toBytes(txWeight + inputWeight + outputWeight).toLong()
    }

    private fun outputSizeByScriptSize(size: Int): Int {
        return 8 + 1 + size            // spentValue + scriptLength + script
    }

    fun witnessSize(type: ScriptType): Int {  // in vbytes
        return when {
            type.isWitness -> witnessData
            else -> legacyWitnessData
        }

    }

    private fun toBytes(fee: Int): Int {
        return (fee / 4) + if (fee % 4 == 0) 0 else 1
    }

    private fun getLockingScriptSize(scriptType: ScriptType): Int {
        return lockingScriptSizes[scriptType] ?: 0
    }

}
