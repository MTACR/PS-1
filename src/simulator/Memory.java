package simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Memory {

    private final short[] memory;
    private short sp; //stackpointer
    private short accessed; //um "RE" atualizado a cada acesso na memory
    private static final short stackZero = 3; //posição do inicio da pilha //definidos assim pra facilitar possiveis modificações
    private static short stackSize = 0; // O tamanho da pilha é definido ao carregar o arquivo.

    public Memory(int size) {
        memory = new short[size];
        sp = stackZero;
    }

    //Pilha
    public void push(short word) {  //insere na pilha
        if (sp <= stackSize + 2) {
            memory[sp] = word;
            sp++; // Só aumenta SP se foi inserido algo na pilha
        }
    }

    public short pop() { //retira da pilha
        if (sp <= stackZero) { //verifica se a pilha está vazia
            return 0;
        } else {
            return memory[--sp];
        }
    }

    public short firstPosition() {
        return (short) (memory[2] + 3);
    }

    //Carrega programa para memória e retorna início da área de dados
    public void loadFileToMemory(File file) {
        if (file != null)
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int i = 0;

                while ((line = reader.readLine()) != null) {
                    memory[i++] = (short) Integer.parseInt(line, 2);
                }

                // TAMANHO DA PILHA É DEFINIDO DEPOIS DE CARREGAR O ARQUIVO
                stackSize = (short) memory[2]; // usar memory[2] para obter o endereço limite da pilha
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        else throw new RuntimeException("Invalid executable file");
    }

    public void storeWord(short pos, short word) {
        memory[pos] = word;
    }

    // retorna um endereço da memória dependendo do modo de endereçamento
    // f1 diz se é direto ou indireto
    /*public short getAddress(int pos, boolean f1) {
        return getWord(pos, f1, false);
    }*/

    public short getWord(int pos, boolean f1, boolean f3) {
        if (pos > memory.length || pos < 0) {
            throw new RuntimeException("Out of bounds");
        }

        short s = memory[pos];

        if (f3) {
            accessed = (short) pos;
            return s;
        } else if (f1) {
            short contentsAddress = memory[s];
            accessed = contentsAddress;
            return memory[contentsAddress];
        } else {
            accessed = s;
            return memory[s];
        }
    }

    public int size() {
        return memory.length;
    }

    short getSp() {
        return sp;
    }

    public short[] getMemory() {
        return memory;
    }

    public short getAccessed() { //chamado nas operações para atualizar o RE com o endereço mais recentemente acessado
        return accessed;
    }

}
