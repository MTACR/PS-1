package linker;

import linker.auxiliar.DefinitionTable;
import simulator.Interface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static linker.FirstPass.*;
import static linker.SecondPass.*;
import loader.Loader;

public class Linker {

    public static void main(String[] args) {
        int offset = 3; //indicador do inicio do programa + nada + tamanho máximo da pilha

        args = new String[]{"output/FATORIAL.obj", "output/VARS.obj"};

        //FirstPass
        ArrayList<Segment> segments = readSegments(args);
        offset += getStackSum(segments);
        DefinitionTable tgs = unifyDefinitions(segments, offset);
        //checkUsages(segments);

        //SecondPass
        ArrayList<Line> lines = updateAddresses(segments, offset);
        updateReferences(lines, segments, tgs);

        //Prints
        System.out.println("Código");
        for(int i = 0; i < lines.size(); i++){
            System.out.println(i + " " + lines.get(i).toString());
        }

        System.out.println("Definições");
        for(Definition def : tgs.values()) {
            System.out.println(def.toString());
        }

        System.out.println("Usos");
        for (Segment seg : segments){
            for(Usage use : seg.usageTable){
                System.out.println(use.toString());
            }
        }
    }

    public static File link(List<File> files) {
        Interface.instance().printMessage("Linking...");

        int offset = 3;

        String[] args = new String[files.size()];

        for (int i = 0; i < args.length; i++)
            args[i] = files.get(i).getPath();

        ArrayList<Segment> segments = readSegments(args);
        offset += getStackSum(segments);
        DefinitionTable tgs = unifyDefinitions(segments, offset);
        //checkUsages(segments);
		
		/*System.out.println(segments.get(0).fileName);
		System.out.println(segments.get(0).stackSize);
		System.out.println(segments.get(0).usageTable);
		System.out.println(segments.get(0).lines);*/
		
        //SecondPass
        ArrayList<Line> lines = updateAddresses(segments, offset);
        updateReferences(lines, segments, tgs);
		
		for (Line l : lines) {
			System.out.println(l);
		}
		
		String outputFile = files.get(0).getName().substring(0, files.get(0).getName().indexOf("."));
		
        return Loader.load(lines, offset, outputFile);
    }
}
