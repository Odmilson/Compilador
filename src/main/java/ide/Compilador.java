package ide;

import gals.LexicalError;
import gals.Lexico;
import gals.SemanticError;
import gals.Semantico;
import gals.Simbolo;
import gals.Sintatico;
import gals.SyntaticError;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author odmil
 */
public class Compilador {

    public static void main(String[] args) {
        Lexico lex = new Lexico();
        Sintatico sint = new Sintatico(); 
        Semantico sem = new Semantico();
        
        lex.setInput("int main(){int cont; cont = 10 - 1;} ");
        
        try {
            sint.parse(lex, sem);
            for (Simbolo sim : sem.getTabelaSimbolos().getListaSimbolos()) {
                System.out.println("Tipo: " + sim.getTipo() + " | Id: " + sim.getId() + " | Escopo: " + sim.getEscopo() + " | Vetor: " + sim.getFlagVetor() + " | Função: " + sim.getFlagFuncao() + " | Parametro: " + sim.getFlagParametro() + " | Inicializada: " + sim.getFlagInicializada() + " | Usada: " + sim.getFlagUsada());
            }
            System.out.println(sem.warnings);
            System.out.println(sem.pontoText);
            System.out.println("Compilado com sucesso");
        } catch (LexicalError ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SyntaticError ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SemanticError ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}














