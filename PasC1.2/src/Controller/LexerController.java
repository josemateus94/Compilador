package Controller;

import Model.InfIdentificador;
import Model.Tag;
import Model.Token;
import java.io.RandomAccessFile;

/**
 *
 * @author xisto
 */
public class LexerController {

    private final int END_OF_FILE = -1;//contante para fim do arquivo, nesse caso retorna -1
    private int lookahead = 0; // armazena o último caractere lido do arquivo	
    public static int linha = 1; // contador de linhas
    public static int coluna = 1; // contador de linhas
    private RandomAccessFile instance_file; // referencia para o arquivo
    private static TabelaSimboloController tabelaSimbolos; // tabela de simbolos
    int cont = 0; //
    boolean primeiraLetra = false;// Faz a validação se a primeira letra do

    /**
     * Efetua a leitura do arquivo selecionado. Caso o mesmo não sejá encontrado
     * ira terona um erro, informando que o arquivo não foi aberto
     *
     * @param input_data - O caminho do arquio que sera analizado.
     *
     * @author Helio Matos - 11511313
     * @author José Mateus - 11423477
     */
    public LexerController(String input_data) {

        // Abre instance_file de input_data
        try {
            instance_file = new RandomAccessFile(input_data, "r");
        } catch (Exception ex) {
            System.out.println("Erro de abertura do arquivo " + input_data + "\n" + ex);
            System.exit(1);
        }
        tabelaSimbolos = new TabelaSimboloController();
    }

    /**
     * Fecha o arquivo de texto de que foi aberto
     *
     * @author Helio Matos - 11511313
     * @author José Mateus - 11423477
     */
    public void fecharArquivo() {

        try {
            instance_file.close();
        } catch (Exception ex) {
            System.out.println("Erro ao fechar arquivo\n" + ex);
            System.exit(3);
        }
    }

    public void printTS() {
        System.out.println("");
        System.out.println("--------Tabela de Simbolos--------");
        System.out.println(tabelaSimbolos.toString());
        System.out.println();
    }

    /**
     * Volta uma posição na leitura do texto
     *
     * @author Helio Matos - 11511313
     * @author José Mateus - 11423477
     */
    public void retornaPosicao() {

        try {
            // Não retornar o ponteiro em caso de Fim de Arquivo
            if (lookahead != END_OF_FILE) {
                instance_file.seek(instance_file.getFilePointer() - 1);
                this.coluna--;
            }
        } catch (Exception ex) {
            System.out.println("Falha ao retornar a leitura\n" + ex);
            System.exit(4);
        }
    }
    public int  l(){
        return this.linha;
    }
    public int c(){
        return this.coluna;
    }
    
    public void printLexer(){
        Token token;        
         do {
            token = this.proximoToken();
            if (token.getClasse() == Tag.ERRO) {
                System.out.println(token.erro() + "\t Linha: " + this.linha + "\t Coluna: " + this.coluna);
            } else {
                System.out.println("Token: " + token.toString() + "\t Linha: " + this.linha + "\t Coluna: " + this.coluna);
            }

        } while (token != null && token.getClasse() != Tag.EOF);
    }

    /**
     * Ira avançar os caracters até encontra END_OF_FILE. O mesmo também faz a
     * validação dos caracter, caso sejá encontra um erro, o mesmo será
     * reportado, informando a linha a coluna e o caracter que era esperado.
     *
     * @return ira retorna o proximo token
     *
     * @author Helio Matos - 11511313
     * @author José Mateus - 11423477
     */
    public Token proximoToken() {
        StringBuilder lexema = new StringBuilder();
        int estado = 0;
        char c;

        while (true) {
            c = '\u0000';

            try {// avanca caractere
                lookahead = instance_file.read();
                if (lookahead != END_OF_FILE) {
                    c = (char) lookahead;
                    c = Character.toLowerCase(c);
                    this.coluna++;
                }
            } catch (Exception ex) {
                System.out.println("Erro ao ler o arquivo");
                System.exit(3);
            }
            switch (estado) {
                case 0:
                    if (lookahead == END_OF_FILE) {
                        return new Token(Tag.EOF, "EOF", linha, coluna);
                    } else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                        switch (c) {
                            case ' ':
                                //coluna++;
                                break;
                            case '\t':
                                coluna += 2;
                                break;
                            case '\n':
                                coluna = 1;
                                linha++;
                                break;
                        }
                    } else if (Character.isLetter(c)) {
                        lexema.append(c);// AGRUPA OS VALORES DESEJADO.
                        //coluna++;
                        estado = 1;

                    } else if (c == '"') {
                        //coluna++;
                        estado = 3;

                    } else if (c == '\'') {
                        //coluna++;
                        estado = 5;

                    } else if (Character.isDigit(c)) {
                        //coluna++;
                        lexema.append(c);
                        estado = 8;

                    } else if (c == '=') {
                        //coluna++;
                        estado = 12;

                    } else if (c == '!') {
                        //coluna++;
                        estado = 15;

                    } else if (c == '>') {
                        //coluna++;
                        estado = 17;

                    } else if (c == '<') {
                        //coluna++;
                        estado = 20;
                    } else if (c == '+') {
                        //coluna++;
                        lexema.append(c);
                        estado = 23;//estado final
                        return new Token(Tag.OP_AD, lexema.toString(), this.linha, this.coluna);

                    } else if (c == '-') {
                        //coluna++;
                        lexema.append(c);
                        estado = 24;//estado final
                        return new Token(Tag.OP_MIN, lexema.toString(), this.linha, this.coluna);

                    } else if (c == '*') {
                        //coluna++;
                        lexema.append(c);
                        estado = 25;//estado final
                        return new Token(Tag.OP_MUL, lexema.toString(), this.linha, this.coluna);

                    } else if (c == '/') {
                        //coluna++;
                        estado = 26;

                    } else if (c == '{') {
                        lexema.append(c);
                        //coluna++;
                        estado = 33;//estado final
                        return new Token(Tag.SMB_OBC, lexema.toString(), this.linha, this.coluna);

                    } else if (c == '}') {
                        lexema.append(c);
                        //coluna++;
                        estado = 34;//estado final
                        return new Token(Tag.SMB_CBC, lexema.toString(), this.linha, this.coluna);

                    } else if (c == '(') {
                        lexema.append(c);
                        //coluna++;
                        estado = 35;//estado final
                        return new Token(Tag.SMB_OPA, lexema.toString(), this.linha, this.coluna);

                    } else if (c == ')') {
                        lexema.append(c);
                        //coluna++;
                        estado = 36;//estado final
                        return new Token(Tag.SMB_CPA, lexema.toString(), this.linha, this.coluna);

                    } else if (c == ',') {
                        lexema.append(c);
                        //coluna++;
                        estado = 37;//estado final
                        return new Token(Tag.SMB_COM, lexema.toString(), this.linha, this.coluna);

                    } else if (c == ';') {
                        lexema.append(c);
                        //coluna++;
                        estado = 38;//estado final
                        return new Token(Tag.SMB_SEM, lexema.toString(), this.linha, this.coluna);
                    } else {
                        System.out.println("-->" + Tag.ERRO + " Simbolo informado incorretamente. " + "Linha " + this.linha + " Coluna " + this.coluna);
                    }

                    break;
                case 1://recebe do estado 0
                    if (Character.isLetterOrDigit(c)) {
                        //coluna++;
                        lexema.append(c);
                    } else {
                        Token token = tabelaSimbolos.retornaToken(lexema.toString());
                        if (!(token == null)) {

                            retornaPosicao();
                            return token;
                        } else {
                            token = new Token(Tag.ID, lexema.toString(), this.linha, this.coluna);
                            tabelaSimbolos.put(token, new InfIdentificador()); // insere novo Token ID na TS
                        }
                        if (Character.isLetterOrDigit(c)) {
                            lexema.append(c);
                        } else if (c == '\n') {
                            retornaPosicao();
                            estado = 2;//estado final                            
                            return new Token(Tag.ID, lexema.toString(), this.linha, this.coluna);
                        } else {
                            retornaPosicao();
                            estado = 2;//estado final                            
                            return new Token(Tag.ID, lexema.toString(), this.linha, this.coluna);
                        }
                        token.setLinha(this.linha);
                        token.setColuna(this.coluna);
                        return token;
                    }
                    break;

                case 3://recebe do estado 0
                    if (c == '"') {
                        estado = 4;//estado final
                        if (cont > 0) {
                            cont = 0;
                            //coluna++;
                            return new Token(Tag.LIT, lexema.toString(), this.linha, this.coluna);
                        } else {
                            cont = 0;
                            return new Token(Tag.ERRO, "LIT deve conter pelo menos um item", this.linha, this.coluna);
                        }
                    } else if (lookahead == END_OF_FILE) {
                        cont = 0;
                        //coluna++;
                        return new Token(Tag.ERRO, "LIT deve ser fechada com \" antes do fim de arquivo", this.linha, this.coluna);
                    } else if (c == '\n') {
                        //System.out.println("-->" + Tag.ERRO + " LIT deve ser fechada com \" antes da quebra de linha. " + "Linha " + this.linha + " Coluna " + this.coluna);
                    } else {
                        estado = 3;
                        lexema.append(c);
                        //coluna++;
                        if (!(c == ' ') && !(c == '\n')) {
                            cont++;
                        }
                    }
                    if (c == '\n') {
                        linha++;
                        //coluna = 1;
                    }
                    break;

                case 5://recebe do 0
                    if (c == '\'') {
                        return new Token(Tag.ERRO, " LIT tem que ter pelo menos 1 que sejá diferente de espaço ou quebra de linha", this.linha, this.coluna);
                    } else if (c == '\n' || c == ' ' || c == '\t') {
                        cont++;
                    } else {
                        //coluna++;
                        lexema.append(c);
                        estado = 6;
                        cont++;
                    }
                    break;

                case 6:// recebe do 5                   
                    if (!(c == '\'')) {
                        cont++;
                    }
                    if (c == '\'') {
                        //coluna++;
                        if (cont == 1) {
                            estado = 7;//estado final
                            cont = 0;
                            return new Token(Tag.CON_CHAR, lexema.toString(), this.linha, this.coluna);
                        } else {
                            estado = 7;//estado final
                            cont = 0;
                            //System.out.println("-->" + Tag.ERRO + " CON_CHAR foi  informado incorretamente" + " Linha " + this.linha + " Coluna " + this.coluna);
                            return new Token(Tag.CON_CHAR, lexema.toString(), this.linha, this.coluna);
                        }

                    } else if (lookahead == END_OF_FILE) {
                        cont = 0;
                        //coluna++;
                        return new Token(Tag.ERRO, "CON_CHAR deve ser fechada com \' antes do fim de arquivo", this.linha, this.coluna);
                    } else if (!(c == '\'')) {
                        //coluna++;
                        //System.out.println("-->" + Tag.ERRO + " CON_CHAR foi  informado incorretamente. " + "Linha " + this.linha + " Coluna " + this.coluna);
                    }
                    if (c == '\n') {
                        linha++;
                        //coluna = 0;
                        //System.out.println("--> fim de linha");
                        //return new Token(Tag.CON_CHAR, lexema.toString(), this.linha, this.coluna);
                    }

                    break;
                case 8://recebe do estado 0
                    if (Character.isDigit(c)) {
                        //coluna++;
                        lexema.append(c);
                    } else if (c == '.') {
                        //coluna++;
                        lexema.append(c);
                        estado = 9;
                    } else {// estado final numero sem ponto
                        //coluna++;
                        retornaPosicao();
                        return new Token(Tag.CON_NUM, lexema.toString(), linha, coluna);
                    }
                    break;

                case 9:// recebe do estado 8                    
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        //coluna++;
                        estado = 10;//estado final contém loop(não e erro)                        
                    } else if (!(Character.isDigit(c))) {
                        //coluna++;
                        //System.out.println("-->" + Tag.ERRO + " Numero com erro. " + "Linha " + this.linha + " Coluna " + this.coluna);
                    } else {
                        retornaPosicao();
                        //coluna++;
                        estado = 0;
                        lexema.deleteCharAt((lexema.length() - 1));
                        //System.out.println("-->" + Tag.ERRO + " Numero com erro. " + "Linha " + this.linha + " Coluna " + this.coluna);
                        return new Token(Tag.CON_NUM, lexema.toString(), linha, coluna);
                    }
                    if (c == '\n') {
                        //coluna = 1;
                        linha++;
                    }

                    break;

                case 10:// recebe do estado 9
                    if (Character.isDigit(c)) {
                        lexema.append(c);
                        //coluna++;
                    } else {
                        retornaPosicao();
                        return new Token(Tag.CON_NUM, lexema.toString(), linha, coluna);
                    }
                    break;

                case 12:// recebe do estado 0
                    if (c == '=') {
                        //coluna++;
                        estado = 13;//estado final
                        return new Token(Tag.OP_EQ, "==", linha, coluna);
                    } else if (true) {
                        estado = 14;//estado final
                        retornaPosicao();
                        return new Token(Tag.OP_ASS, "=", linha, coluna);
                    }
                    break;

                case 15:// recebe do estado 0
                    if (c == '=') {
                        //coluna++;
                        estado = 16;//estado final
                        return new Token(Tag.OP_NE, "!=", linha, coluna);
                    } else if (lookahead == END_OF_FILE) {
                        return new Token(Tag.ERRO, "OP_NE esperava um igual =  antes do final do arquivo. ", this.linha, this.coluna);
                    } else if (!(c == '=')) {
                        estado = 15;
                        //coluna++;
                        //System.out.println("-->" + Tag.ERRO + " OP_NE esperava um igual = " + "Linha " + this.linha + " Coluna " + this.coluna);
                    } else if (true) {
                        retornaPosicao();
                        return new Token(Tag.ERRO, "Após a '!' obrigatoriamente tem que vim '=' ", linha, coluna);
                    }
                    if (c == '\n') {
                        linha++;
                        //coluna = 0;
                    }
                    if (c == '\t') {
                        //coluna += 3;
                    }

                    break;

                case 17:// recebe do estado 0
                    if (c == '=') {
                        //coluna++;
                        estado = 18;//estado final
                        return new Token(Tag.OP_GE, ">=", linha, coluna);
                    } else if (true) {
                        estado = 19;//estado fial
                        retornaPosicao();
                        return new Token(Tag.OP_GT, ">", linha, coluna);
                    }
                    break;

                case 20:// recebe do estado 0
                    if (c == '=') {
                        //coluna++;
                        estado = 21;//estado final
                        return new Token(Tag.OP_LE, "<=", linha, coluna);
                    } else if (true) {
                        estado = 22;//estado final
                        retornaPosicao();
                        return new Token(Tag.OP_LT, "<", linha, coluna);
                    }
                    break;
                case 26:// recebe do estado 0
                    if (c == '/') {
                        linha++;
                        estado = 28;
                    } else if (c == '*') {
                        estado = 30;
                    } else {
                        estado = 27;//estado final
                        retornaPosicao();
                        return new Token(Tag.OP_DIV, "/", linha, coluna);
                    }
                    break;

                case 28:
                    if (!(c == '\n')) {

                    } else {
                        estado = 0; //estado final 29
                    }
                    break;

                case 30:
                    if ((c == '*')) {
                        estado = 31;
                    } else if (this.lookahead == END_OF_FILE) {
                        estado = 31;
                    } else {
                        estado = 30;
                    }
                    if (c == '\n') {
                        linha++;
                    }
                    break;

                case 31:
                    if (c == '/') {
                        estado = 0; //estado final 32;
                    } else if (this.lookahead == END_OF_FILE) {//colocar fim de arquivo erro
                        return new Token(Tag.ERRO, "Comentario deve ser fechado", this.linha, this.coluna);
                    } else {
                        estado = 30;
                    }
                    break;
            }
        }
    }
}
