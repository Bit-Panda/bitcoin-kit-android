package io.horizontalsystems.bitcoinkit

import io.horizontalsystems.bitcoincore.models.Block
import io.horizontalsystems.bitcoincore.network.Network
import io.horizontalsystems.bitcoincore.storage.BlockHeader
import io.horizontalsystems.bitcoincore.utils.HashUtils

class MainNet : Network() {

    override var port: Int = 9333

    override var magic: Long = 0xa2f3b4f5L
    override var bip32HeaderPub: Int = 0x0488B21E   // The 4 byte header that serializes in base58 to "xpub".
    override var bip32HeaderPriv: Int = 0x0488ADE4  // The 4 byte header that serializes in base58 to "xprv"
    override var addressVersion: Int = 0
    override var addressSegwitHrp: String = "bc"
    override var addressScriptVersion: Int = 5
    override var coinType: Int = 0

    override val maxBlockSize = 1_000_000
    override val dustRelayTxFee = 3000 // https://github.com/bitcoin/bitcoin/blob/c536dfbcb00fb15963bf5d507b7017c241718bf6/src/policy/policy.h#L50

    override var dnsSeeds = listOf(
            "seed1.bitcoinv.org.",
            "seed2.bitcoinv.org.",
            "seed3.bitcoinv.org.",
            "zpool.ca",
            "atomminer.com",
            "aod-tech.com",
            "miningcoins.ca",
            "blockminer.me",
            "aiomine.com",
            "seed4.bitcoinv.org.",
            "seed5.bitcoinv.org.",
            "seed6.bitcoinv.org.",
            "seed7.bitcoinv.org.",
            "seed8.bitcoinv.org."
    )

    override val bip44CheckpointBlock = Block(BlockHeader(
            version = 2,
            previousBlockHeaderHash = HashUtils.toBytesAsLE("000000004c2738ff52ee6dc039d4fde2f3292fed9afa9d712f895d7094f8d350"),
            merkleRoot = HashUtils.toBytesAsLE("34032d78c6b5119c316ad1ed1dc30612bb24a2366c2d927fd9f47593c6422900"),
            timestamp = 1573687111,
            bits = 486604799,
            nonce = 3509954292,
            hash = HashUtils.toBytesAsLE("000000000013864717b44ff0a19141d25ae2b591b505cc2e5a5232f8269cbe42")
    ), 1)

}
