package io.horizontalsystems.dashkit.core

import io.horizontalsystems.bitcoincore.core.BaseTransactionInfoConverter
import io.horizontalsystems.bitcoincore.core.ITransactionInfoConverter
import io.horizontalsystems.bitcoincore.extensions.toHexString
import io.horizontalsystems.bitcoincore.models.InvalidTransaction
import io.horizontalsystems.bitcoincore.models.Transaction
import io.horizontalsystems.bitcoincore.models.TransactionStatus
import io.horizontalsystems.bitcoincore.storage.FullTransactionInfo
import io.horizontalsystems.dashkit.instantsend.InstantTransactionManager
import io.horizontalsystems.dashkit.models.DashTransactionInfo

class DashTransactionInfoConverter(private val instantTransactionManager: InstantTransactionManager) : ITransactionInfoConverter {
    override lateinit var baseConverter: BaseTransactionInfoConverter

    override fun transactionInfo(fullTransactionInfo: FullTransactionInfo): DashTransactionInfo {
        val transaction = fullTransactionInfo.header

        if (transaction.status == Transaction.Status.INVALID) {
            (transaction as? InvalidTransaction)?.let {
                return getInvalidTransactionInfo(it)
            }
        }

        val txInfo = baseConverter.transactionInfo(fullTransactionInfo)

        return DashTransactionInfo(
                txInfo.uid,
                txInfo.transactionHash,
                txInfo.transactionIndex,
                txInfo.from,
                txInfo.to,
                txInfo.amount,
                txInfo.fee,
                txInfo.blockHeight,
                txInfo.timestamp,
                txInfo.status,
                instantTransactionManager.isTransactionInstant(fullTransactionInfo.header.hash)
        )
    }

    private fun getInvalidTransactionInfo(transaction: InvalidTransaction): DashTransactionInfo {
        return try {
            DashTransactionInfo(transaction.serializedTxInfo)
        } catch (ex: Exception) {
            DashTransactionInfo(
                    uid = transaction.uid,
                    transactionHash = transaction.hash.toHexString(),
                    transactionIndex = transaction.order,
                    timestamp = transaction.timestamp,
                    status = TransactionStatus.INVALID,
                    from = listOf(),
                    to = listOf(),
                    amount = 0,
                    fee = 0,
                    blockHeight = null,
                    instantTx = false
            )
        }
    }

}