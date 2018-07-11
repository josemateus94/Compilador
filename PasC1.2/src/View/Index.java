package View;

import Controller.LexerController;
import Controller.ParserController;

/**
 * @author Helio Matos - 11511313
 * @author José Mateus - 11423477
 */
public class Index {

    /**
     * Efetua a instancia de lexer, Token etabela de simbolos. Efetua a
     * empressão dos das validação e dos erros.
     *
     * @param args -
     */
    public static void main(String[] args) {
        LexerController lexer = new LexerController("PasC1_ok.jvn");// efetua a instancia do lexer e informa o arquivo que será lido.  
        //lexer.printLexer();

        ParserController parser = new ParserController(lexer);
        parser.prog();
        parser.fechaArquivos();

        lexer.printTS();
        lexer.fecharArquivo();
    }
}
