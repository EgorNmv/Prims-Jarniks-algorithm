package com.company;

import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Map;

public class Main {

    public static int count_of_All_Points;
    public static ArrayList all_Points = new ArrayList();
    public static int matrix [][];
    static HashMap<Integer,ArrayList> connected_componnts = new HashMap<>(); //название + вершины
    public static HashMap<Integer,String> result = new HashMap<>();
    static boolean maxvalue = true;
    public static int weight = 0;
    static int count = 1;
    public static ArrayList visited_available_vertex = new ArrayList();

    public static void main(String[] args) {
        reader();
        makeMatrix();
        distanceOfPoints();
        jarnik_Prim_Dijkstra();
        try {
            writeResult();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reader() {
        try{
            FileInputStream fstream = new FileInputStream("in.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            count_of_All_Points = Integer.parseInt(br.readLine());
            matrix = new int[count_of_All_Points][count_of_All_Points];
            String strLine = br.readLine();
            while (strLine != null){
                parser(strLine);
                strLine = br.readLine();
            }
        }catch (IOException e){
            System.out.println("Ошибка");
        }
    }

    public static void parser(String str) {
        while (str != "") {
            int num = str.indexOf(" ");
            if (num == -1){
                break;
            }
            String times = str.substring(0, num);
            str = str.substring(num+1);
            all_Points.add(Integer.parseInt(times));
        }
        all_Points.add(Integer.parseInt(str));
    }

    public static void makeMatrix() {
        matrix = new int[count_of_All_Points + 1][count_of_All_Points + 1];
        for (int i=0; i<count_of_All_Points + 1; i++) {
            for (int j=0; j<count_of_All_Points + 1; j++) {
                matrix[i][j] = -1;
            }
        }
    }

    public static void distanceOfPoints() {
        for (int i=0; i<all_Points.size(); i+=2) {
            for (int j=0; j<all_Points.size(); j+=2){
                if (i != j) {
                    int xfirst=(int)all_Points.get(i);
                    int yfirst=(int)all_Points.get(i+1);
                    int xsecond=(int)all_Points.get(j);
                    int ysecond=(int)all_Points.get(j+1);

                    matrix[i/2 + 1][j/2 + 1]=Math.abs(xfirst-xsecond)+Math.abs(yfirst-ysecond);
                    matrix[j/2 + 1][i/2 + 1]=matrix[i/2 + 1][j/2 + 1];
                }
            }
        }
    }

    public static void jarnik_Prim_Dijkstra() {
        for (int i = 1; i <= count_of_All_Points; i++){
            result.put(i,"");
        }
        while (maxvalue) {
            process();
        }
        sortedHashMap();
    }

    public static void process () {
        visited_available_vertex.add(1);//пулл доступных вершин, из которых возможен поиск
        int min = Integer.MAX_VALUE;
        int currvertexi=0;
        int currvertexj=0;
        for (int i=0; i<visited_available_vertex.size(); i++) {
            for (int j=0; j<=count_of_All_Points; j++) {
                if ((matrix[(int) visited_available_vertex.get(i)][j] < min) && (matrix[(int) visited_available_vertex.get(i)][j] != -1)) {
                    min=matrix[(int) visited_available_vertex.get(i)][j];
                    currvertexj=j;
                    currvertexi=(int) visited_available_vertex.get(i);
                }
            }
        }
        if (min == Integer.MAX_VALUE){
            maxvalue = false;
            return;
        }
        arrangementVertex(min,currvertexi,currvertexj);

    }

    public static void arrangementVertex (int ver, int x, int y) {
        if (checkconected_compontsVertexUpdate(x,y)){ // Проверяем наличие в  connected_componntsVertex
            weight = weight + ver;
            String times = result.get(x) + y + " ";
            result.put(x,times);
            times = result.get(y) + x + " ";
            result.put(y,times);
        }
        matrix[x][y] = -1;
        matrix[y][x] = -1;
        visited_available_vertex.add(y);// запомнить использованные значения из матрицы

    }


    public static boolean checkconected_compontsVertexUpdate(int x, int y) {
        int on = setUpdate(x);
        int to = setUpdate(y);
        if ((on == to) && (on != -1)){ //не можем работать тк создадут цикл
            return false;
        }
        if ((on == to) && (on == -1)){ //не прин комп связности, создаём комп связн и добавляем её
            ArrayList times = new ArrayList();
            times.add(x);
            times.add(y);
            connected_componnts.put(count,times);
            count++;
            return true;
        }
        if (on != to){ //в разных комп связности
            if ((on != -1) && (to != -1)){ //в разных но оба лежат, то объединяем
                ArrayList onAr = connected_componnts.get(on);
                ArrayList toAr = connected_componnts.get(to);
                for (int i = 0; i < toAr.size(); i++){
                    onAr.add(toAr.get(i));
                }
                connected_componnts.remove(to);
                return true;
            }
            if ((on == -1) && (to != -1)){//добавляем в который лежит тот
                connected_componnts.get(to).add(x);
                return true;
            }

            if ((on != -1) && (to == -1)){//добавляем в который лежит тот
                connected_componnts.get(on).add(y);
                return true;
            }
        }
        return false;
    }

    private static int setUpdate (int value){
        for (Map.Entry<Integer,ArrayList> time : connected_componnts.entrySet()){//обходим все элементы conected_componts
            ArrayList name = time.getValue();
            for (int j = 0; j < name.size(); j++){ // Нулевой элемент это название компонент связности
                if (value == (int)name.get(j)){
                    return time.getKey();
                }
            }
        }
        return -1;
    }

    public static void sortedHashMap () {
        for (Map.Entry<Integer,String> time : result.entrySet()){
            String proc = time.getValue();
            int ind = proc.indexOf(" ");
            ArrayList sorted = new ArrayList();
            while (ind != -1){
                sorted.add(Integer.parseInt(proc.substring(0,ind)));
                proc = proc.substring(ind+1);
                ind = proc.indexOf(" ");
            }
            String resultS = "";
            sorted.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            });
            for (int i = 0; i < sorted.size(); i++){
                resultS = resultS + sorted.get(i) + " ";
            }
            resultS = resultS + 0;
            result.put(time.getKey(),resultS);
        }
    }

    public static void writeResult () throws IOException {
        File file = new File("out.txt");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        for (Map.Entry<Integer,String> time : result.entrySet()){
            writer.write(time.getKey() + " " + time.getValue()+"\n");
        }
        writer.write(String.valueOf(weight));
        writer.flush();
        writer.close();
    }

}
