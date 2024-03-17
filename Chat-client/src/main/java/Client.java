

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private final Socket socket;   // Socket дескриптор для связи между клиентом и сервером по протоколу IP.
    private final String name;     // Name Client

    /**
     * BufferedWriter — это класс из библиотеки Java, который используется для записи данных в поток байтов.
     * Он работает как буфер между приложением и физическим устройством, улучшая производительность системы.
     */
    private BufferedWriter bufferedWriter;

    /** BufferedReader — это класс из библиотеки Java,
    * предназначенный для чтения данных из потока байтов.*/
    private BufferedReader bufferedReader;
    /**
     * Конструктор, создающий нового клиента, который подключается к серверу через указанный сокет (socket).
     * При этом создается поток чтения и поток записи для обмена данными между клиентом и сервером.
     * Если при создании потоков возникает ошибка, то закрываются все открытые ресурсы (closeEverything).
     *
     * @param socket   — дескриптор для связи между клиентом и сервером по протоколу IP.
     * @param userName — имя клиента.
     */
    public Client(Socket socket, String userName){
        this.socket = socket;
        name = userName;
        try
        {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }


    }

    /**
     * Слушатель для входящих сообщений
     */
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (socket.isConnected()){
                    try {
                        message = bufferedReader.readLine();
                        System.out.println(message);
                    }
                    catch (IOException e){
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    /**
     * Отправить сообщение
     */
    public void sendMessage(){
        try {
            bufferedWriter.write(name);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                bufferedWriter.write(name + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    /**
     * Метод closeEverything закрывает входящий и исходящий потоки,
     * если переменная не равна нулю и вызывала исключение.
     * После этого закрывается socket.
     *
     * @param socket         —> дескриптор для связи между клиентом и сервером по протоколу IP.
     * @param bufferedReader —> это класс из библиотеки Java, предназначенный для чтения данных из потока байтов.
     * @param bufferedWriter —> это класс из библиотеки Java, который используется для записи данных в поток байтов.
     */
    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
