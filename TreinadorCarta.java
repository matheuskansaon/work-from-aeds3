/*Classe filha de Carta.java,que armazena dados de uma carta que necessariamente
*é de treinador.*/

import java.text.SimpleDateFormat;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.util.Locale;
import java.util.Scanner;

class TreinadorCarta extends Carta{
    //---------------
    //Atributos
    //---------------

    String tipoTrainer;//Indica qual tipo de carta de treinador está carta é.Ex:Estádio(Stadium),Item,Apoiador(Supporter),dentre outros
    String efeito;//Indica qual o efeito desta carta de treinador no jogo.
   
    //--------------
    //Construtor
    //--------------

    TreinadorCarta(String[] info,int numb){
        super(info,numb);
        tipoTrainer = info[0];
        String effect = "";//Montar uma string com efeito desta carta
        if(!info[12].isEmpty() && info[12].compareTo("null")!=0){
            int count = 0,virgulaCount = 0;
            int size = info[12].length();
            while(count<size){
                 if(info[12].charAt(count)==','){
                    virgulaCount++;
                    if(virgulaCount == 4){
                        while(info[12].charAt(count)!=':'){
                            count++;
                        }
                        count+=3;
                        while(info[12].charAt(count)!='"'){
                            effect+=info[12].charAt(count);
                            count++;
                        }
                    }else if(virgulaCount == 5){
                        effect+='@';
                        virgulaCount = 0;
                    }

                    
                 }
                count++;
            }
        }
        efeito = effect;
    }

    //Construtor usado quando ocorre a leitura do arquivo sequencial
    TreinadorCarta(byte[] arr,int identificacao){
        super(arr,identificacao);
        try{
        ByteArrayInputStream bais = new ByteArrayInputStream(arr);
        DataInputStream dis = new DataInputStream(bais);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy",Locale.US);
        tipoTrainer = dis.readUTF();
        nome = dis.readUTF();
        numeracao = dis.readInt();
        colecao = dis.readUTF();
        illustrator = dis.readUTF();
        efeito = dis.readUTF();
        dataLancamento = sdf.parse(dis.readUTF());
        id = identificacao;
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //-----------
    //Metodos
    //-----------
    
    /* paraByteArray -> converta as informações desta carta para um array de bytes.
    * @return (byte[]) array de bytes da carta
    */
    public byte[] paraByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy",Locale.US);
        dos.writeUTF(tipoTrainer);
        dos.writeUTF(nome);
        dos.writeInt(numeracao);
        dos.writeUTF(colecao);
        dos.writeUTF(illustrator);
        dos.writeUTF(efeito);
        dos.writeUTF(sdf.format(dataLancamento));
        return baos.toByteArray();
    }
    /* mostrar -> mostra todas as informações desta carta na saída padrão.
    */
    public void mostrar(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy",Locale.US);
        System.out.println("ID: "+ id + " / Tipo de treinador: " + tipoTrainer);
        System.out.println("Nome: "+ nome +" / Numeração: " + numeracao);
        System.out.println("Ilustrador(a): " + illustrator + "/ Coleção original: " + colecao);
        System.out.println("Efeito:"  + efeito +"/Data de lançamento: " + sdf.format(dataLancamento));
    }
    /* modificar -> modifica alguns atributos desta classe,que o úsuario decidir 
    * @param (Scanner s) Scanner passado desde PTCGUI,para não dar erro de leitura dupla
    * @return (byte[]) array de bytes representando as mudanças nesta carta
    */
    public byte[] modificar(Scanner s) throws IOException{
        int escolha = 0;
        do{
            System.out.println("----------------------------------");
            System.out.println("Por favor,escolha qual atributo você");
            System.out.println("deseja mudar desta carta:");
            System.out.println("1 - Tipo de treinador da carta:");
            System.out.println("2 - Nome da carta:");
            System.out.println("3 - Numeração da carta:");
            System.out.println("4 - Coleção original desta carta:");
            System.out.println("5 - Efeitos desta carta:");
            System.out.println("6 - Data de lançamento desta carta");
            System.out.println("7 - Ilustrador(a) desta carta:");
            System.out.println("0 - Para sair:");
            System.out.println("(Digite o número e aperte Enter)");
            System.out.println("----------------------------------");
            escolha = Integer.parseInt(s.nextLine());
            switch(escolha){
                case 1:
                System.out.println("Tipo de treinador atual: " + tipoTrainer);
                System.out.println("Digite um novo tipo de treinador(String)");
                tipoTrainer = s.nextLine();
                break;
                case 2:
                System.out.println("Nome atual: " + nome);
                System.out.println("Digite um novo nome(String)");
                nome = s.nextLine();
                break;
                case 3:
                System.out.println("Numeração atual: " + numeracao);
                System.out.println("Digite uma nova numeração(int)");
                numeracao = Integer.parseInt(s.nextLine());
                break;
                case 4:
                System.out.println("Coleção original desta carta agora: " + colecao);
                System.out.println("Digite uma nova coleção original(String)");
                colecao = s.nextLine();
                break;
                case 5:
                System.out.println("Efeito atual desta carta: "+ efeito);
                System.out.println("Digite um novo efeito(String)");
                efeito = s.nextLine();
                break;
                case 6:
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy",Locale.US);
                System.out.println("Data de lançamento atual desta carta: "+sdf.format(dataLancamento));
                System.out.println("Digite o novo dia de lançamento(int)(Respeitando número de dias num mês)");
                int dia = Integer.parseInt(s.nextLine());
                System.out.println("Digite o novo mês de lançamento(int)(Respeitando número de meses num ano)");
                int mes = Integer.parseInt(s.nextLine());
                System.out.println("Digite o novo ano de lançamento(int)");
                int ano = Integer.parseInt(s.nextLine());
                boolean valid;
                String mesString = "";
                //Verificar se data é válida
                switch(mes){
                    case 1:
                    if(dia>0 && dia <=31){
                            valid = true;
                            mesString = "Jan";
                    }else{
                        valid = false;
                    }
                    break;
                    case 2:
                    //Checar anos bissextos,para saber quantos dias em fevereiro
                    if(ano%4==0 &&(!(ano%100 == 0) || ano%400 == 0)){
                        if(dia>0 && dia <=29){
                            valid = true;
                            mesString = "Feb";
                    }else{
                        valid = false;
                    }
                    }else{
                        if(dia>0 && dia <=28){
                            valid = true;
                            mesString = "Feb";
                    }else{
                        valid = false;
                    }
                    }
                    break;
                    case 3:
                    if(dia>0 && dia <=31){
                            valid = true;
                            mesString = "Mar";
                    }else{
                        valid = false;
                    }
                    break;
                    case 4:
                    if(dia>0 && dia <=30){
                            valid = true;
                            mesString = "Apr";
                    }else{
                        valid = false;
                    }
                    break;
                    case 5:
                    if(dia>0 && dia <=31){
                            valid = true;
                            mesString = "May";
                    }else{
                        valid = false;
                    }
                    break;
                    case 6:
                    if(dia>0 && dia <=30){
                            valid = true;
                            mesString = "Jun";
                    }else{
                        valid = false;
                    }
                    break;
                    case 7:
                    if(dia>0 && dia <=31){
                            valid = true;
                            mesString = "Jul";
                    }else{
                        valid = false;
                    }
                    break;
                    case 8:
                    if(dia>0 && dia <=31){
                            valid = true;
                            mesString = "Aug";
                    }else{
                        valid = false;
                    }
                    break;
                    case 9:
                    if(dia>0 && dia <=30){
                            valid = true;
                            mesString = "Sep";
                    }else{
                        valid = false;
                    }
                    break;
                    case 10:
                    if(dia>0 && dia <=31){
                            valid = true;
                            mesString = "Oct";
                    }else{
                        valid = false;
                    }
                    break;
                    case 11:
                    if(dia>0 && dia <=30){
                            valid = true;
                            mesString = "Nov";
                    }else{
                        valid = false;
                    }
                    break;
                    case 12:
                    if(dia>0 && dia <=31){
                            valid = true;
                            mesString = "Dec";
                    }else{
                        valid = false;
                    }
                    break;    
                    default:
                    valid = false;
                    break;
                
                }
                if(valid){
                    String temporary = dia + " " + mesString +","+ano;
                    try{
                    dataLancamento = sdf.parse(temporary);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    System.out.println("Data inválida");
                }
                break;
                case 7:
                System.out.println("Ilustrador(a) atual desta carta: " + illustrator);
                System.out.println("Digite um(a) novo(a) ilustrador(a)(String)");
                illustrator = s.nextLine();
                break;
                case 0:
                System.out.println("Saindo e atualizando registro...");
                break;
                default:
                System.out.println("Escolha inválida");
                break;
            }
        }while(escolha!=0);
        return this.paraByteArray();
    }
}