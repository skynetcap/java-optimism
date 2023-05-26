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

    private static final Web3j web3 = Web3j.build(new HttpService("https://mainnet.optimism.io"));
    private static final Logger LOGGER = LoggerFactory.getLogger(OptimismMain.class);

    public static void main(String[] args) throws IOException {
        // token
        LOGGER.info("ChainID: " + web3.ethChainId().send().getChainId().toString());
        LOGGER.info("0x715a Balance: " +
                web3.ethGetBalance("0x2a82d937A12D28E75cdb45dA9A2B1357d34D715a",
                DefaultBlockParameter.valueOf("latest")
        ).send().getBalance().doubleValue());

        LOGGER.info("Optimism Latest TX");
        List<EthBlock.TransactionResult> txs =
                web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true)
                        .send()
                        .getBlock()
                        .getTransactions();

        txs.forEach(tx -> {
            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
            LOGGER.info("TX: " + transaction.getHash());
            LOGGER.info("From: " + transaction.getFrom());
            LOGGER.info("To: " + transaction.getTo());
            LOGGER.info("Raw: " + transaction.getInput());

            try {
                String code = web3.ethGetCode(transaction.getTo(),
                        DefaultBlockParameter.valueOf("latest")).send().getCode();
                LOGGER.info("'To' Contract bytecode: " + code);
                decompileBytecode(code);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void decompileBytecode(String code) {
        String inputCode = code.substring(2);
        for (int i = 0; i < inputCode.length(); i+=2) {
            // read 2 chars as a switch
            switch (inputCode.charAt(i) + inputCode.charAt(i + 1)) {
                case 0x00 -> LOGGER.info("null");
                case 0x36 -> LOGGER.info("CALLDATASIZE");
                default -> LOGGER.info("???: " + (byte) (inputCode.charAt(i) + inputCode.charAt(i + 1)));
            }
        }
    }
}
