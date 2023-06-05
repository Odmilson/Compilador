package gals;

import java.util.ArrayList;

public class Pilha {
    private ArrayList<Integer> stack;

    public Pilha() {
        stack = new ArrayList<>();
    }

    public void push(int elemento) {
        stack.add(elemento);
    }
    
    public int topo() {
        if (!isEmpty()) {
            int indiceTopo = stack.size() - 1;
            return stack.get(indiceTopo);
        } else {
            throw new IndexOutOfBoundsException("A pilha está vazia!");
        }
    }

    public int pop() {
        if (!isEmpty()) {
            int indiceTopo = stack.size() - 1;
            int elementoTopo = stack.get(indiceTopo);
            stack.remove(indiceTopo);
            return elementoTopo;
        } else {
            throw new IndexOutOfBoundsException("A pilha está vazia!");
        }
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public boolean encontrar(Integer dado) {
        
        for (int i = stack.size() - 1; i >= 0; i--) {
           if(dado == stack.get(i))
               return true;
        }
        
        return false;
    }
}
