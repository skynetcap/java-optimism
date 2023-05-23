import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.List;

public class OptimismMain {
    public static void main(String[] args) throws IOException {
        Web3j web3 = Web3j.build(new HttpService("https://mainnet.optimism.io"));
        Logger LOGGER = LoggerFactory.getLogger(OptimismMain.class);

        // token
        LOGGER.info("ChainID: " + web3.ethChainId().send().getChainId().toString());
        LOGGER.info("0x715a Balance: " +
                web3.ethGetBalance("0x2a82d937A12D28E75cdb45dA9A2B1357d34D715a",
                DefaultBlockParameter.valueOf("latest")
        ).send().getBalance().doubleValue());

        LOGGER.info("Optimism Latest TX");
        List<EthBlock.TransactionResult> txs =
                web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getBlock().getTransactions();
        txs.forEach(tx -> {
            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
            LOGGER.info("TX: " + transaction.getHash());
            LOGGER.info("From: " + transaction.getFrom());
            LOGGER.info("To: " + transaction.getTo());
            LOGGER.info("Raw: " + transaction.getInput());
        });
    }
}
