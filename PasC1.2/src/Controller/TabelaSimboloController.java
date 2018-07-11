package Controller;

import Model.InfIdentificador;
import Model.Tag;
import Model.Token;
import java.util.HashMap;

/**
 *
 * @author Helio Matos - 11511313
 * @author José Mateus - 11423477
 */
public class TabelaSimboloController {

    private HashMap<Token, InfIdentificador> tabelaSimbolos;

    /**
     * Responsavel por conter as palavras reservado, nesse caso as KW.
     *
     * @author Helio Matos - 11511313
     * @author José Mateus - 11423477
     */
    public TabelaSimboloController() {

        tabelaSimbolos = new HashMap();
        Token word;
        word = new Token(Tag.KW_program, "program", 0, 0);
        this.tabelaSimbolos.put(word, new InfIdentificador());

        word = new Token(Tag.KW_if, "if", 0, 0);
        this.tabelaSimbolos.put(word, new InfIdentificador());

        word = new Token(Tag.KW_else, "else", 0, 0);
        this.tabelaSimbolos.put(word, new InfIdentificador());

        word = new Token(Tag.KW_while, "while", 0, 0);
        this.tabelaSimbolos.put(word, new InfIdentificador());

        word = new Token(Tag.KW_read, "read", 0, 0);
        this.tabelaSimbolos.put(word, new InfIdentificador());

        word = new Token(Tag.KW_num, "num", 0, 0);
        this.tabelaSimbolos.put(word, new InfIdentificador());

        word = new Token(Tag.KW_char, "char", 0, 0);
        this.tabelaSimbolos.put(word, new InfIdentificador());

        word = new Token(Tag.KW_not, "not", 0, 0);
        this.tabelaSimbolos.put(word, new InfIdentificador());

        word = new Token(Tag.KW_or, "or", 0, 0);
        this.tabelaSimbolos.put(word, new InfIdentificador());

        word = new Token(Tag.KW_and, "and", 0, 0);
        this.tabelaSimbolos.put(word, new InfIdentificador());
        
        word = new Token(Tag.KW_write, "write", 0, 0);
        this.tabelaSimbolos.put(word, new InfIdentificador());
    }

    public void put(Token w, InfIdentificador i) {
        tabelaSimbolos.put(w, i);
    }

    // Retorna um identificador de um determinado token
    public InfIdentificador getIdentificador(Token w) {
        InfIdentificador infoIdentificador = (InfIdentificador) tabelaSimbolos.get(w);
        return infoIdentificador;
    }

    /**
     *
     * @param lexema - recebe como parametro o lexema que foi gerado no case 1.
     * @return token - caso o token sejá valido o mesmo será retornado caso
     * contrario ira retorna null.
     *
     * @author Helio Matos - 11511313
     * @author José Mateus - 11423477
     */
    public Token retornaToken(String lexema) {
        for (Token token : tabelaSimbolos.keySet()) {
            if (token.getLexema().equals(lexema)) {
                return token;
            }
        }
        return null;
    }

    /**
     * E feito um Override no toString que faz a junção da posicao e do
     * token.toString()
     *
     * @return saida - retorna a junção de todos os elementos.
     * @author Helio Matos - 11511313
     * @author José Mateus - 11423477
     */
    @Override
    public String toString() {
        String saida = "";
        int i = 1;
        for (Token token : tabelaSimbolos.keySet()) {
            saida += ("posicao " + i + ": \t" + token.toString()) + "\n";
            i++;
        }
        return saida;
    }
}
