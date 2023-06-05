package gals;

import java.util.List;
import java.util.Stack;
import java.util.ArrayList;

public class TabelaSimbolos {

    private List<Simbolo> listaSimbolos = new ArrayList<Simbolo>();

    public List<Simbolo> getListaSimbolos()
    {
        return listaSimbolos;
    }

    public void setListaSimbolos(List<Simbolo> listaSimbolos)
    {
        this.listaSimbolos = listaSimbolos;
    }

    //Adiciona a variavel na lista
    public Boolean add(Simbolo simbolo)
    {
        if(listaSimbolos.add(new Simbolo(
                simbolo.getTipo(),
                simbolo.getId(), 
                simbolo.getEscopo(),
                simbolo.getFlagVetor(),
                simbolo.getFlagFuncao(),
                simbolo.getFlagParametro(),
                simbolo.getFlagInicializada(),
                simbolo.getFlagUsada()
                )
            )
        ) return true;

        return false;
    }

    //Verifica se a variavel pode ser declarada
    public Boolean declarar(Simbolo simbolo, Stack<Integer> pilhaEscopo)
    {
    
        if(!simbolo.getFlagFuncao()) //Se não for função
        {
            if(!listaSimbolos.stream().filter( 
                    s -> (s.getId().equals(simbolo.getId()))     //Verifica se os Ids são iguais
                    &&(pilhaEscopo.search(s.getEscopo()) != -1) //Verifica se a variavel é visivel no escopo atual
                    &&(s.getEscopo() == simbolo.getEscopo())    //Verifica se os escopos são iguais
                    &&(!s.getFlagVetor())
                ).toList().isEmpty()
            ) return false;
            
            return true;
        }

        //Busca a função na lista
        if(listaSimbolos.stream().filter(

            s -> (s.getId() == simbolo.getId())
                &&(s.getFlagFuncao())
            ).findFirst() != null

        ) return true;

        return false;
    }

    //Verifica se a variavel foi declarada
    public Boolean declarada(Simbolo simbolo, Stack<Integer> pilhaEscopo)
    {
    
        if(!simbolo.getFlagFuncao()) //Se não for função
        {
            if(!simbolo.getFlagVetor()){    
                if(!listaSimbolos.stream().filter( 
                        s -> (s.getId().equals(simbolo.getId()))     //Verifica se os Ids são iguais
                        &&(pilhaEscopo.search(s.getEscopo()) != -1) //Verifica se a variavel é visivel no escopo atual
                        &&(!s.getFlagVetor())
                    ).toList().isEmpty()
                ) 
                {   
                    return true;
                }
            }
            else
            {
                if(!listaSimbolos.stream().filter( 
                        s -> (s.getId().equals(simbolo.getId()))     //Verifica se os Ids são iguais
                        &&(pilhaEscopo.search(s.getEscopo()) != -1) //Verifica se a variavel é visivel no escopo atual
                        &&(s.getFlagVetor())
                    ).toList().isEmpty()
                ) 
                {   
                    return true;
                }
            }
            
            return false;
        }

        //Busca a função na lista
        if(listaSimbolos.stream().filter(

            s -> (s.getId() == simbolo.getId())
                &&(s.getFlagFuncao())
            ).findFirst() != null

        ) return true;

        return false;
    }


    //Define a variavel como Usada  Retorna  1: Sucesso
    //                              Retorna  0: Sucesso, porém a variável não esta inicializada
    //                              Retorna -1: Fracasso, variável não encontrada)
    public Integer setUsada(Simbolo simbolo, Stack<Integer> pilhaEscopo)
    {
        if(!simbolo.getFlagFuncao()) //Se não for função
        {
            List<Simbolo> simbolos;

            if(!simbolo.getFlagVetor()){
                //Procura a variavel na lista, e todas que encontrar adiciona na lista
                simbolos = listaSimbolos.stream().filter( 
                        s -> (s.getId().equals(simbolo.getId()))     //Verifica se os ids são iguais
                        &&(pilhaEscopo.search(s.getEscopo()) != -1) //Verifica se a variavel é visivel no escopo atual
                        &&(!s.getFlagFuncao())
                        &&(!s.getFlagVetor())
                    ).toList();
            }
            else
            {
                //Procura o Vetor na lista, e todas que encontrar adiciona na lista
                simbolos = listaSimbolos.stream().filter( 
                        s -> (s.getId().equals(simbolo.getId()))     //Verifica se os ids são iguais
                        &&(pilhaEscopo.search(s.getEscopo()) != -1) //Verifica se a variavel é visivel no escopo atual
                        &&(!s.getFlagFuncao())
                        &&(s.getFlagVetor())
                    ).toList();
            }

            //Verifica se encontrou alguma variável
            if(!simbolos.isEmpty())
            {
                //Define a ultima variável encontrada como usada
                //Preferencia sempre ao escopo mais interno
                simbolos.get(simbolos.size() - 1).setFlagUsada(true);

                //Verifica se a variável foi inicializada
                if(!simbolos.get(simbolos.size() - 1).getFlagInicializada())
                    return 0;

                return 1;
            }

            return -1;
        }

        //Procura a função na lista e caso encontre, salva em um Simbolo
        List<Simbolo> simboloFromList = listaSimbolos.stream().filter(
            s -> (s.getId().equals(simbolo.getId()))
                &&(s.getFlagFuncao())
            ).toList();            

        //Verifica se a função foi encontrada
        if(simboloFromList != null)
        {
            //Define a fuunção como Usada
            simboloFromList.get(0).setFlagUsada(true);
            return 1;
        }

        return -1;
    }

    //Define a variavel como Inicializada 
    public Boolean setInicializada(String id, Boolean isVet)
    {
        List<Simbolo> simbolos;
        if(!isVet){
            //Procura a variavel na lista, e todas que encontrar adiciona na lista
            simbolos = listaSimbolos.stream().filter( 
                        s -> (s.getId().equals(id))     //Verifica se os nomes são iguais
                            &&(!s.getFlagVetor())
                    ).toList();
        }
        else
        {
            //Procura o vetor na lista, e todas que encontrar adiciona na lista
            simbolos = listaSimbolos.stream().filter( 
                        s -> (s.getId().equals(id))     //Verifica se os nomes são iguais
                            &&(!s.getFlagVetor())
                    ).toList();
        }


        //Verifica se encontrou alguma variável
        if(!simbolos.isEmpty())
        {   
            //Define a ultima variável encontrada como inicializada
            //Preferencia sempre ao escopo mais interno
            simbolos.get(simbolos.size() - 1).setFlagInicializada(true);
            return true;
        }

        return false;
    }

    //Retorna o tipo da variável
    public int getTipo(String id, Boolean isFuncao, Boolean isVet, Stack<Integer> pilhaEscopo)
    {
        String tipo;

        if(!isFuncao)
        {
            if(!isVet)
            {
                List<Simbolo> simbolos = listaSimbolos.stream().filter( 
                        s -> (s.getId().equals(id))     //Verifica se os ids são iguais
                        &&(pilhaEscopo.search(s.getEscopo()) != -1) //Verifica se a variavel é visivel no escopo atual
                        &&(!s.getFlagFuncao())
                        &&(!s.getFlagVetor())
                    ).toList();
                
                tipo = simbolos.get(simbolos.size() - 1).getTipo();
            }
            else
            {
                List<Simbolo> simbolos = listaSimbolos.stream().filter( 
                        s -> (s.getId().equals(id))     //Verifica se os ids são iguais
                        &&(pilhaEscopo.search(s.getEscopo()) != -1) //Verifica se a variavel é visivel no escopo atual
                        &&(!s.getFlagFuncao())
                        &&(s.getFlagVetor())
                    ).toList();
                
                tipo = simbolos.get(simbolos.size() - 1).getTipo();
            }

        }
        else
        {
            List<Simbolo> simbolos = listaSimbolos.stream().filter(
            s -> (s.getId().equals(id))
                &&(s.getFlagFuncao())
            ).toList();
            
            tipo = simbolos.get(0).getTipo();
        }

        switch (tipo){
            case "int" -> {
                return 0;
            }
            
            case "float" -> {
                return 1;
            }
            
            case "char" -> {
                return 2;
            }
            
            case "string" -> {
                return 3;
            }
            
            case "bool" -> {
                return 4; 
            }
            
            case "void" -> {
                return -1;
            }
        }

        return -1;
    }

}
