package Model;

/**
 * Nome dos Token
 *
 * @author Helio Matos - 11511313
 * @author José Mateus - 11423477
 */
public enum Tag {

    // fim de arquivo
    EOF,
    //Operadores
    OP_EQ, // ==
    OP_NE, //!=
    OP_GT, // >
    OP_LT, //<
    OP_GE, // >=
    OP_LE, //<=
    OP_AD, //+
    OP_MIN, //-
    OP_MUL, //*
    OP_DIV, ///
    OP_ASS,// =

    //Simbolos
    SMB_OBC, //{
    SMB_CBC, //}
    SMB_OPA, //(
    SMB_CPA, //)
    SMB_COM, //,
    SMB_SEM, //;

    //Palavras-chave
    KW_program,
    KW_if, 
    KW_else, 
    KW_while, 
    KW_write, 
    KW_read, 
    KW_num, 
    KW_char, 
    KW_not, 
    KW_or, 
    KW_and,

    //Identificadores
    ID,
    
    //Literal
    LIT,
    
    //Constantes 
    CON_NUM,// num_const
    CON_CHAR, //char_const

    //Erro
    ERRO

}
