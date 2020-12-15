package linker;

import linker.auxiliar.DefinitionTable;
import linker.auxiliar.UsageTable;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FirstPass {
    public static ArrayList<Segment> readSegments(String[] fileNames){ //Protótipo de leitura de leitura, considerando que as tabelas de definição e uso estão em um arquivo diferente do código objeto
        ArrayList<Segment> segments = new ArrayList<>();
        for(String fileNameObj : fileNames) {
            DefinitionTable definitionTable = new DefinitionTable();
            UsageTable usageTable = new UsageTable();
            ArrayList<Line> lines = new ArrayList<>();
            int length = 0;

            String fileNameTbl = fileNameObj.replace(".obj",".tbl");

            try {
                File fileObj = new File(fileNameObj);
                File fileTbl = new File(fileNameTbl);
                String line;
                String[] sl;

                try (BufferedReader readerObj = new BufferedReader(new FileReader(fileObj))) {

                    while ((line = readerObj.readLine()) != null) {
                        sl = line.split(" ");

                        int address = Integer.parseInt(sl[0]);
                        int size = Integer.parseInt(sl[1]);

                        length += size;

                        for (int i = 2; i < (size * 2) + 2; ) {
                            int op = Integer.parseInt(sl[i++]);
                            char mode = sl[i++].charAt(0);
                            lines.add(new Line(op, mode));
                        }
                    }
                }

                try (BufferedReader readerTbl = new BufferedReader(new FileReader(fileTbl))) {

                    while((line = readerTbl.readLine()) != null){
                        sl = line.split(" ");
                        char flag = sl[2].charAt(0);

                        if(flag == 'a' || flag == 'r'){
                            Definition def = new Definition(sl[0], Integer.parseInt(sl[1]), flag);
                            definitionTable.put(def.symbol, def);
                        } else {
                            if(flag == '+' || flag == '-'){
                               usageTable.add(new Usage(sl[0], Integer.parseInt(sl[1]), flag));
                            } else {
                                throw new IOException("Undefined flag: " + flag);
                            }
                        }
                    }
                }

            } catch (IOException | NumberFormatException e) {
                final JPanel panel = new JPanel();
                JOptionPane.showMessageDialog(panel, "Arquivo inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }

            segments.add(new Segment(fileNameObj, definitionTable, usageTable, lines, length));
        }
        return segments;
    }

    //Unifica as tabelas de definições dos vários segmentos em única tabela (tabela de símbolos globais)
    //Tabela de definições é copiada para a TSG (1ª tabela copiada sem alterações, na 2ª tabela o valor dos endereços é adicionado do tamanho do primeiro segmento)
    public static DefinitionTable unifyDefinitions(ArrayList<Segment> segments, int offset) {
        DefinitionTable tgs = new DefinitionTable();    //Tabela de Símbolos Globais (TSG): Armazena todos os símbolos globais definidos. União das tabelas de definição dos diferentes segmentos.

        try {
            for (Segment seg : segments) {

                for (Definition def : seg.definitionTable.values()) {

                    if (tgs.get(def.symbol) == null) {
                        def.offset(offset);
                        tgs.put(def.symbol, def);

                    } else {
                        throw new Exception("Redefined Symbol in " + seg.fileName + ": " + def.symbol);
                    }
                }

                offset += seg.length;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return tgs;
    }

    //INUTIL o teste já é feito no updateReferences()
    //Checa se as entradas na tabela de uso bate com as da definição, buscando por indefinidas
    /*public static void checkUsages(ArrayList<Segment> segments){ //Provavelmente desnecessario, deve ser possivel fazer esse teste em uma etada posterior do ligador
        try {
            for (Segment segUse : segments) {

                for (Map.Entry use : segUse.usageTable.entrySet()) {
                    String key = (String) use.getKey();
                    boolean found = false;

                    for (Segment segDef : segments) {

                        if (segUse != segDef) { //Bem provavelmente desnecessario
                            if (segDef.definitionTable.get(key) != null) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        throw new Exception("Undefined Symbol in " + segUse.fileName + ": " + key + "'s definition not found");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
