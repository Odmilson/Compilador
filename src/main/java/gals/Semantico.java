package gals;

import java.util.Stack;

public class Semantico implements Constants
{
    private final Simbolo variavel = new Simbolo();
    private final Simbolo temporario = new Simbolo();
    private final TabelaSimbolos tabelaSimbolos = new TabelaSimbolos();

    private final Stack<Integer> pilhaEscopo = new Stack<Integer>();
    private Integer contadorEscopo = 0;
    private final Stack<Integer> pilhaTipo= new Stack<Integer>();
    private Integer tipoAtribuicao;
    private final Stack<Integer> pilhaOperacao = new Stack<Integer>();

    private Boolean inicio = true;
    private Boolean declaracao = false;
    private Boolean atribuicaoIsVet;

    public String warnings = "";
    private String idAtribuicao;

    public void executeAction(int action, Token token)	throws SemanticError
    {
        System.out.println("Ação #"+action+", Token: "+token);

        if(inicio){
            pilhaEscopo.push(contadorEscopo);
            inicio = false;
        }

        switch(action)
        {
            //Salva o tipo da variável
            case 1 -> {
            
                variavel.setTipo(token.getLexeme());

            }

            //Salva a variável a ser manipulada
            case 2 -> {

                variavel.setId(token.getLexeme());
                variavel.setEscopo(this.pilhaEscopo.peek()); //Salva o escopo em que a variável está inserida
                
                //inicializa ela como uma variável padrão
                variavel.setFlagVetor(false);
                variavel.setFlagFuncao(false);
                variavel.setFlagParametro(false);
                variavel.setFlagInicializada(false);
                variavel.setFlagUsada(false);
            }

            //define a variável como vetor
            case 3 -> {

                variavel.setFlagVetor(true);

            } 

            //define a variável como função
            case 4 -> {

                variavel.setFlagFuncao(true);

            }

            //define a variável como parametro
            case 5 -> {
                
                variavel.setEscopo(variavel.getEscopo() + 1); //incrementa o escopo em +1 poque o parametro faz parte do escopo da função
                variavel.setFlagParametro(true);
                variavel.setFlagInicializada(true); //considera que o parametro ja é passado inicializado

            }

            //Adiciona a variavel na tabela
            case 6 -> {
                
                if(tabelaSimbolos.declarar(variavel, pilhaEscopo) //Verifica se á variavel ainda não foi declarada
                    || tabelaSimbolos.getListaSimbolos().isEmpty()) 
                {   
                    tabelaSimbolos.add(variavel);
                    declaracao = false;
                    break;
                }
                
                throw new SemanticError (
                    String.format("Variavel %s já declarada", variavel.getId()), 
                    token.getPosition()
                );

            }
            
            //sinaliza quando a operação é: declaração
            case 7 -> {

                declaracao = true;

            }

            //define a variavel como usada
            case 8 -> {

                Integer aux = tabelaSimbolos.setUsada(variavel, pilhaEscopo);    
                pilhaTipo.push(tabelaSimbolos.getTipo(variavel.getId(), variavel.getFlagFuncao(), variavel.getFlagVetor(), pilhaEscopo));

                if(pilhaTipo.peek() == -1)
                    throw new SemanticError ("Atribuição de função sem retorno",token.getPosition());

                if(aux == -1) //Caso não encontre a variavel gera erro
                    throw new SemanticError (
                        String.format("Variavel %s não declarada", variavel.getId()), 
                        token.getPosition()
                    );
                else if(aux == 0)
                    warnings += String.format("Variavel %s utilizada sem ser inicializada\n", 
                                                variavel.getId()
                                            );

            }

            //Verifica se a variavel ja foi declarada
            case 9 -> {
                if(!declaracao){
                    if(!tabelaSimbolos.declarada(variavel, pilhaEscopo)) //Caso não encontre a variavel gera erro
                        throw new SemanticError (
                            String.format("Variavel %s não declarada", variavel.getId()), 
                            token.getPosition()
                        );
                    idAtribuicao = variavel.getId();
                    atribuicaoIsVet = variavel.getFlagVetor();
                    tipoAtribuicao = tabelaSimbolos.getTipo(variavel.getId(), variavel.getFlagFuncao(), variavel.getFlagVetor(), pilhaEscopo);
                    break;
                }
                
                if(tabelaSimbolos.declarar(variavel, pilhaEscopo) //Verifica se á variavel ainda não foi declarada
                    || tabelaSimbolos.getListaSimbolos().isEmpty()) 
                {   
                    tabelaSimbolos.add(variavel);
                    idAtribuicao = variavel.getId();
                    atribuicaoIsVet = variavel.getFlagVetor();
                    tipoAtribuicao = tabelaSimbolos.getTipo(variavel.getId(), variavel.getFlagFuncao(), variavel.getFlagVetor(), pilhaEscopo);
                    declaracao = false;
                    break;
                }
                
                throw new SemanticError (
                    String.format("Variavel %s já declarada", variavel.getId()), 
                    token.getPosition()
                );


            }

            //Define a variável que recebe a atribuição como inicializada
            case 10 -> {

                System.out.println(tipoAtribuicao);
                System.out.println(pilhaTipo.peek());
                
                int resultAtrib = SemanticTable.atribType(tipoAtribuicao, pilhaTipo.pop());
                System.out.println(resultAtrib);
                if(resultAtrib != SemanticTable.ERR){

                    if(resultAtrib == SemanticTable.WAR){
                        warnings += String.format("Atribuindo um float a um inteiro, possivel perca de precisão (Pos: %d)\n", token.getPosition());
                    }

                    
                    if(!tabelaSimbolos.setInicializada(idAtribuicao, atribuicaoIsVet))
                        throw new SemanticError (
                            String.format("Variavel %s não declarada", variavel.getId()), 
                            token.getPosition()
                        );

                }else {
                    throw new SemanticError ("Atribuição de tipos incompativeis", token.getPosition());
                }
                
            }

            case 11 -> {

                contadorEscopo++;
                pilhaEscopo.push(contadorEscopo);

            }

            case 12 -> {

                pilhaEscopo.pop();

            }

            case 13 -> {

                this.pilhaTipo.push(SemanticTable.INT);

            }

            case 14 -> {

                this.pilhaTipo.push(SemanticTable.FLO);

            }
            
            case 15 -> {

                this.pilhaTipo.push(SemanticTable.CHA);

            }
            
            case 16 -> {

                this.pilhaTipo.push(SemanticTable.STR);

            }
            
            case 17 -> {

                this.pilhaTipo.push(SemanticTable.BOO);

            }
            
            case 18 -> {

                this.pilhaOperacao.push(SemanticTable.SUM);

            }
            
            case 19 -> {

                this.pilhaOperacao.push(SemanticTable.SUB);

            }
            
            case 20 -> {

                this.pilhaOperacao.push(SemanticTable.MUL);

            }
            
            case 21 -> {

                this.pilhaOperacao.push(SemanticTable.DIV);

            }
            
            case 22 -> {

                this.pilhaOperacao.push(SemanticTable.REL);

            }

            case 23 -> {

                while(!pilhaOperacao.isEmpty()){

                    int tipo2 = this.pilhaTipo.pop();
                    int operacao = this.pilhaOperacao.pop();
                    int tipo1 = this.pilhaTipo.pop();

                    int resultExp = SemanticTable.resultType(tipo1, tipo2, operacao);

                    if(resultExp != SemanticTable.ERR){
                        this.pilhaTipo.push(resultExp);
                    }else{
                        throw new SemanticError ("Operação de tipos incompativeis");
                    }

                }

            }

            case 24 -> {

                if(!tabelaSimbolos.setInicializada(variavel.getId(), variavel.getFlagVetor()))
                    throw new SemanticError (
                        String.format("Variavel %s não declarada", variavel.getId()), 
                        token.getPosition()
                    );

            }

            case 25 -> {

                Integer aux = tabelaSimbolos.setUsada(variavel, pilhaEscopo);    

                if(aux == -1) //Caso não encontre a variavel gera erro
                    throw new SemanticError (
                        String.format("Variavel %s não declarada", variavel.getId()), 
                        token.getPosition()
                    );
                else if(aux == 0)
                    warnings += String.format("Variavel %s utilizada sem ser inicializada\n", 
                                                variavel.getId()
                                            );

            }

        }

    }
    
    public TabelaSimbolos getTabelaSimbolos()
    {
        return tabelaSimbolos;
    }
}
