package Controller;
import Controller.LexerController;
import Model.Tag;
import Model.Token;
import View.Index;
import java.util.ArrayList;

/**
 *
 * @author xisto
 */
public class ParserController {

    private final LexerController lexer;
    private Token token;
    private ArrayList<Tag> tagsSincronizantes;
    int cont =1;
    
    public ParserController(LexerController lexer) {
        this.lexer = lexer;
        token = lexer.proximoToken(); // Leitura inicial obrigatoria do primeiro simbolo
        System.out.println("[DEBUG]" + token.toString() + " linha " + lexer.l() + " e coluna " + lexer.c());
        tagsSincronizantes = new ArrayList<Tag>();
    }

    // Fecha os arquivos de entrada e de tokens
    public void fechaArquivos() {
        //lexer.fecharArquivo();
    }

    public void erroSintatico(String mensagem) {

        System.out.println("[Erro Sintatico] na linha " + lexer.l() + " e coluna " + lexer.c());
        System.out.println(mensagem + "\n");
    }

    public void advance() {
        token = lexer.proximoToken();
        System.out.println("[DEBUG]" + token.toString() + " linha " + lexer.l() + " e coluna " + lexer.c());
    }

    // verifica token esperado t
    public boolean eat(Tag t) {
        if (token.getClasse() == t) {
            advance();
            return true;
        } else {
            return false;
        }
    }

    public void skip(String mensagem) {
        erroSintatico(mensagem);
        advance();
    }

    public void sincronizaToken(String mensagem) {
        boolean casaToken = false;

        while (!casaToken && token.getClasse() != Tag.EOF) {
            if (tagsSincronizantes.contains(token.getClasse())) {
                casaToken = true;
            } else {
                skip(mensagem);
            }
        }
        tagsSincronizantes.clear(); // limpa a lista para a proxima sincronizacao
    }

    /* ----------------------------------------------------- //
    // Todas as tomadas de decisao do ParserController, sao guiadas
    // pela Tabela Preditiva.
    // ----------------------------------------------------- /*/
    // prog → “program” “id” body
    public void prog() {
        //System.out.println("[DEBUG] Programa()");

        if (token.getClasse() == Tag.KW_program) { // espera program

            if (!(this.eat(Tag.KW_program))) {
                erroSintatico("Esperado program, encontrado " + token.getLexema());
                System.exit(1);
            }

            if (!(this.eat(Tag.ID))) {
                erroSintatico("Esperado ID , encontrado " + token.getLexema());
                System.exit(1);
            }
            body();
        } else {
            erroSintatico("Esperado 'program' , encontrado " + token.getLexema());
            System.exit(1);
            //cont++;
            //erroSintatico("Esperado program, encontrado " + token.getLexema());
            //tagsSincronizantes.add(Tag.EOF);
            //sincronizaToken("[Modo Panico] Esperado \"EOF\", encontrado " + token.getLexema());                       
        }
    }

    // body → decl-list “{“ stmt-list “}”
    public void body() {
        declList();
        if (!(this.eat(Tag.SMB_OBC))) {
            erroSintatico("Esperado abre colchete { , encontrado " + token.getLexema());
            System.exit(1);
        } else {
            if ((token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while) || token.getClasse() == Tag.KW_read
                    || token.getClasse() == Tag.KW_write) {
                stmtList();
            } else if (!(this.eat(Tag.SMB_CBC))) {
                erroSintatico("Esperado 'ID','IF','WHILE','READ','}' , encontrado " + token.getLexema());
                System.exit(1);
            }
        }
    }

    //decl-list → decl “;” decl-list | ε
    public void declList() {

        if (token.getClasse() == Tag.KW_num || token.getClasse() == Tag.KW_char) {
            decl();
            if ((this.eat(Tag.SMB_SEM))) {// compara ponto e virgula
                declList();
            } else {
                erroSintatico("Esperado ';' , encontrado " + token.getLexema());
                System.exit(1);
            }
        } else if (token.getClasse() == Tag.KW_num || token.getClasse() == Tag.KW_char || token.getClasse() == Tag.SMB_OBC) {
            return;
        } else {
            erroSintatico("Esperado 'Num', 'Char', '{' , encontrado " + token.getLexema() + "'");
            System.exit(1);
        }
    }

    //decl → type id-list
    public void decl() {
        type();
        idList();
    }

    //type → “num” | “char”
    public void type() {
        if (!(this.eat(Tag.KW_num) || this.eat(Tag.KW_char))) {
            erroSintatico("Esperado 'numero','char', encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //id-list → “id” id-listLINHA
    public void idList() {
        if (this.eat(Tag.ID)) {
            if (token.getClasse() == Tag.SMB_COM) {//compara a virgula
                idListLinha();
            } else if (token.getClasse() == Tag.SMB_SEM) {
                return;
            } else {
                erroSintatico("Esperado ',' ';' , encontrado " + token.getLexema());
                System.exit(1);
            }
        } else {
            erroSintatico("Esperado 'ID' , encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    public void idListLinha() {
        if ((this.eat(Tag.SMB_COM))) {// consome virgula
            idList();
        } else if (this.eat(Tag.SMB_OBC)) { // compara com abre colchete
            return;
        } else {
            erroSintatico("Esperado virgula ',' , encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //stmt-list → stmt “;” stmt-list | ε
    public void stmtList() {
        //System.out.println("stmtList()");
        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.KW_if || token.getClasse() == Tag.KW_while || token.getClasse() == Tag.KW_read
                || token.getClasse() == Tag.KW_write) {
            stmt();
            if (this.eat(Tag.SMB_SEM)) {//compara ponto e virgula
                stmtList();
            } else {
                erroSintatico("Esperado ';','+','-','OR','*','/','and','ID','(' , encontrado " + token.getLexema());
                System.exit(1);
            }
        } else if (token.getClasse() == Tag.SMB_CBC) { // compara com fecha colchete
            return;
        } else {
            erroSintatico("Esperado fecha colchete 'ID','IF','WHILE','READ','WRITE','}' , encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //stmt → assign-stmt | if-stmt | while-stmt | read-stmt | write-stmt
    public void stmt() {
        //System.out.println("stmt()");
        if (token.getClasse() == Tag.ID) {
            assignStmt();
        } else if (token.getClasse() == Tag.KW_if) {
            ifStmt();
        } else if (token.getClasse() == Tag.KW_while) {
            whileStmt();
        } else if (token.getClasse() == Tag.KW_read) {
            readStmt();
        } else if (token.getClasse() == Tag.KW_write) {
            writeStmt();
        } else {
            erroSintatico("Esperado 'ID','IF','WHILE','READ' , encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //assign-stmt → “id” “=” simple_expr
    public void assignStmt() {
        //System.out.println("assignStmt()");
        if ((this.eat(Tag.ID))) {
            if (this.eat(Tag.OP_ASS)) {//consome o igual
                simpleExpr();
            } else {
                erroSintatico("Esperado '=' , encontrado " + token.getLexema());
                System.exit(1);
            }
        }
    }

    //simple-expr → term simple-exprLINHA
    public void simpleExpr() {
        //System.out.println("simpleExpr()");
        term();
        simpleExprLinha();
    }

    //term → factor-a termLINHA
    public void term() {
        //System.out.println("term()");
        factorA();
        termLinha();

    }

    //factor-a → factor | “not” factor
    public void factorA() {
        //System.out.println("factorA()");
        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.CON_NUM || token.getClasse() == Tag.CON_CHAR || token.getClasse() == Tag.SMB_OPA) {
            factor();
        } else if (token.getClasse() == Tag.KW_not) {
            this.eat(Tag.KW_not);
            factor();
        } else {
            erroSintatico("Esperado 'ID','CON_NUM','CON_CHAR','(',.....'NOT', encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //factor → “id” | constant | “(“ expression “)”
    public void factor() {
        //System.out.println("factor()");
        if (this.eat(Tag.ID)) {
            return;
        } else if (this.eat(Tag.SMB_OPA)) {// consome abre parenteces
            expression();
            if (!(this.eat(Tag.SMB_CPA))) {//consome fecha parenteces
                erroSintatico("Esperado ')', encontrado " + token.getLexema());
                System.exit(1);
            }
        } else if (this.eat(Tag.CON_NUM) || this.eat(Tag.CON_CHAR)) {
            constant();
        } else {
            erroSintatico("Esperado 'ID','NUM','CHAR','(', encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //constant → “num_const” | “char_const”
    public void constant() {
        //System.out.println("constant()");
        return;
    }

    //expression→ simple-expr expressionLINHA
    public void expression() {
        //System.out.println("expression()");
        simpleExpr();
        expressionLinha();
    }

    //expressionLINHA → relop simple-expr | ε
    public void expressionLinha() {
        //System.out.println("expressionLinha()");
        if (token.getClasse() == Tag.OP_EQ || token.getClasse() == Tag.OP_GT || token.getClasse() == Tag.OP_GE || token.getClasse() == Tag.OP_LT
                || token.getClasse() == Tag.OP_LE || token.getClasse() == Tag.OP_NE) {
            relop();
            simpleExpr();
        } else if (!(token.getClasse() == Tag.SMB_CPA)) {
            erroSintatico("Esperado '==','>','>=','<','<=','!=',')', encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //relop → “==” | “>” | “>=” | “<” | “<=” | “!=”
    public void relop() {
        //System.out.println("relop()");
        if (this.eat(Tag.OP_EQ) || this.eat(Tag.OP_GT) || this.eat(Tag.OP_GE) || this.eat(Tag.OP_LT) || this.eat(Tag.OP_LE) || this.eat(Tag.OP_NE)) {
            return;
        }
    }

    //termLINHA → mulop fator-a termLINHA | ε
    public void termLinha() {
        //System.out.println("termLinha()");
        if (token.getClasse() == Tag.OP_MUL || token.getClasse() == Tag.OP_DIV || token.getClasse() == Tag.KW_and) {
            mulop();
            factorA();
            termLinha();
        } else if (!(token.getClasse() == Tag.OP_AD || token.getClasse() == Tag.OP_MIN || token.getClasse() == Tag.KW_or || token.getClasse() == Tag.OP_EQ
                || token.getClasse() == Tag.OP_GT || token.getClasse() == Tag.OP_GE || token.getClasse() == Tag.OP_LT || token.getClasse() == Tag.OP_LE
                || token.getClasse() == Tag.OP_NE || token.getClasse() == Tag.SMB_SEM || token.getClasse() == Tag.SMB_CPA)) {

            erroSintatico(" '+','-','OR',','==','>','>=','<','<=','!=',')',';', encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //mulop → “*” | “/” | “and”
    public void mulop() {
        //System.out.println("mulop()");
        if (this.eat(Tag.OP_MUL) || this.eat(Tag.OP_DIV) || this.eat(Tag.KW_and)) {
            return;
        }
    }

    //simple-exprLINHA → addop term simple-exprLINHA | ε
    public void simpleExprLinha() {
        //System.out.println("simpleExprLinha()");
        if (token.getClasse() == Tag.OP_AD || token.getClasse() == Tag.OP_MIN || token.getClasse() == Tag.KW_or) {
            addop();
            term();
            simpleExprLinha();
        } else if (!(token.getClasse() == Tag.OP_EQ || token.getClasse() == Tag.OP_GT || token.getClasse() == Tag.OP_GE || token.getClasse() == Tag.OP_LT
                || token.getClasse() == Tag.OP_LE || token.getClasse() == Tag.OP_NE || token.getClasse() == Tag.SMB_SEM || token.getClasse() == Tag.SMB_CPA)) {

            erroSintatico("Esperado '+','-','OR',';','==','>','>=','<','<=','!=', ), encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //addop → “+” | “-” | “or”
    public void addop() {
        //System.out.println("addop()");
        if (this.eat(Tag.OP_AD) || this.eat(Tag.OP_MIN) || this.eat(Tag.KW_or)) {
            return;
        }
    }

    //if-stmt → “if” “(“ condition “)” “{“ stmt-list “}“ if-stmtLINHA
    public void ifStmt() {
        //System.out.println("ifStmt()");
        if (this.eat(Tag.KW_if)) {
            if (this.eat(Tag.SMB_OPA)) {//consome abre parenteces (
                expression();
                if (this.eat(Tag.SMB_CPA)) {// consome fecha parenteces )                    
                    if (this.eat(Tag.SMB_OBC)) { // consome abre colchete
                        stmtList();
                        if (this.eat(Tag.SMB_CBC)) { // compara com fecha colchete                          
                            ifStmtLinha();
                        } else {// veio do classe stmtList()
                            erroSintatico("Esperado fecha colchete '}', encontrado " + token.getLexema());
                            System.exit(1);
                        }
                    } else {
                        erroSintatico("Esperado '{', encontrado " + token.getLexema());
                        System.exit(1);
                    }
                } else {
                    erroSintatico("Esperado 'if', encontrado " + token.getLexema());
                    System.exit(1);
                }
            } else {
                erroSintatico("Esperado '(', encontrado " + token.getLexema());
                System.exit(1);
            }
        }
    }

    //condition → expression
    public void condition() {
        //System.out.println("condition()");
        expression();
    }

    //if-stmtLINHA → “else” “{“ stmt-list “}” | ε
    public void ifStmtLinha() {
        //System.out.println("ifStmtLinha()");
        if (token.getClasse() == Tag.KW_else) {
            if (this.eat(Tag.KW_else)) {
                if (this.eat(Tag.SMB_OBC)) {//consome abre colchete
                    stmtList();
                    if (this.eat(Tag.SMB_CBC)) {// consome fecha colchete                       
                        if (token.getClasse() == Tag.SMB_SEM) {
                        } else {
                            erroSintatico("Esperado ';', encontrado " + token.getLexema());
                            System.exit(1);
                        }
                    } else {
                        erroSintatico("Esperado '}', encontrado " + token.getLexema());
                        System.exit(1);
                    }
                } else {
                    erroSintatico("Esperado '{', encontrado " + token.getLexema());
                    System.exit(1);
                }
            }
        } else if (!((token.getClasse() == Tag.KW_else || token.getClasse() == Tag.SMB_SEM))) {
            erroSintatico("Esperado ';', encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //while-stmt → stmt-prefix “{“ stmt-list “}”
    public void whileStmt() {
        //System.out.println("whileStmt()");
        stmtPrefix();
        if (this.eat(Tag.SMB_OBC)) {//consome abre colchete 
            stmtList();
            if (this.eat(Tag.SMB_CBC)) {
                if (token.getClasse() == Tag.SMB_SEM) {
                } else {
                    erroSintatico("Esperado ';', encontrado " + token.getLexema());
                    System.exit(1);
                }
            } else {
                erroSintatico("Esperado '{', encontrado " + token.getLexema());
                System.exit(1);
            }
        } else {
            erroSintatico("Esperado '{', encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //stmt-prefix → “while” “(“ condition “)”
    public void stmtPrefix() {
        //System.out.println("stmtPrefix()");
        if (this.eat(Tag.KW_while)) {
            if (this.eat(Tag.SMB_OPA)) {//consome abre parenteces (
                expression();
                if (this.eat(Tag.SMB_CPA)) {// consome fecha parenteces )                       

                } else {
                    erroSintatico("Esperado ')', encontrado " + token.getLexema());
                    System.exit(1);
                }
            } else {
                erroSintatico("Esperado '(', encontrado " + token.getLexema());
                System.exit(1);
            }
        }
    }

    //read-stmt → “read” “id”
    public void readStmt() {
        //System.out.println("readStmt()");
        if (this.eat(Tag.KW_read)) {
            if (this.eat(Tag.ID)) {
                return;
            } else {
                erroSintatico("Esperado 'ID', encontrado " + token.getLexema());
                System.exit(1);
            }
        } else {
            erroSintatico("Esperado 'read', encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //write-stmt → “write” writable
    public void writeStmt() {
        //System.out.println("writeStmt()");
        if (this.eat(Tag.KW_write)) {
            writable();
        } else {
            erroSintatico("Esperado 'WRITE', encontrado " + token.getLexema());
            System.exit(1);
        }
    }

    //writable → simple-expr | “literal”
    public void writable() {
        //System.out.println("writable()");
        if (token.getClasse() == Tag.ID || token.getClasse() == Tag.CON_NUM || token.getClasse() == Tag.CON_CHAR || token.getClasse() == Tag.SMB_OPA
                || token.getClasse() == Tag.KW_not) {
            simpleExpr();
        } else if (this.eat(Tag.LIT)) {
            if (token.getClasse() == Tag.SMB_SEM) {
            } else {
                erroSintatico("Esperado ';', encontrado " + token.getLexema());
                System.exit(1);
            }
        } else {
            erroSintatico("Esperado 'ID','CON_NUM','CON_CHAR','(','LIT',.....'NOT', encontrado " + token.getLexema());
            System.exit(1);
        }
    }
}
