//Classe que cuida da interface do usuário
//É usada diretamente pela main,que está em PTCGCRUD.java
import java.util.Scanner;

import java.io.RandomAccessFile;
import java.io.File;

class PTCGUI{
  //Métodos
  /*init() -> Método que engloba o ponto de partida e de repetição do programa.
  * Após ele acabar,uma breve mensagem é exibida no metodo main
  * e programa acaba.
  */
  void init(){
    Scanner sc = new Scanner(System.in);//Scanner será usado apenas para ler input
    //do usuário.Leitura da base de dados será feita de outra forma
    int escolhaUser;
    do{
      menu();
      escolhaUser = Integer.parseInt(sc.nextLine());
      switch(escolhaUser){
        case 1:{
          create();
        }
        break;
        case 2:
        {
          System.out.println("Entre com o id do registro a ser lido: ");
          int iden = Integer.parseInt(sc.nextLine());
          read(iden);
        }
        break;
        case 3:
        {
          System.out.println("Entre com o id do registro a ser atualizado: ");
          int iden = Integer.parseInt(sc.nextLine());
          update(iden,sc);
        }
        break;
        case 4:
        {
          System.out.println("Entre com o id do registro a ser deletado: ");
          int iden = Integer.parseInt(sc.nextLine());
          delete(iden);
        }
        break;
        case 5:{
          int option = 0;
          System.out.println("1- Intercalação balanceada comum.");
          System.out.println("2- Intercalação balanceada com blocos diferente");
          System.out.println("3- Intercalação balanceada com seleção por substituição");
          System.out.println("Aperte 4 para sair");
          System.out.print("Entre com sua opção: ");
          option = Integer.parseInt(sc.nextLine());
          if(option != 4)
            ordenar(option);
        
        } break;
        case 0:
        {
          System.out.println("Saindo...");
        }
        break;
        default:
        System.out.println("Escolha inválida!");
        break;
      }
    }while(escolhaUser!=0);//Repete o menu enquanto o usuário não escolher sair com 0
  }
  /*menu() -> Método visual,apenas mostra um menuzinho para o usuário,toda vez
  que ele puder fazer a escolha entre as opções do CRUD
  (C)reate -> 1 | (R)ead -> 2 | (U)pdate -> 3 | (D)elete -> 4
  Sair -> 0
  */
  void menu(){
    System.out.println("----------------------------------");
    System.out.println("Por favor,escolha qual opção você");
    System.out.println("deseja realizar do CRUD:");
    System.out.println("1 - Para criar[(C)reate] a base de dados(BD):");
    System.out.println("2 - Para ler[(R)ead] um registro da BD:");
    System.out.println("3 - Para atualizar[(U)pdate] um registro da BD:");
    System.out.println("4 - Para deletar[(D)elete] um registro da BD:");
    System.out.println("5 - Ordene o arquivo");
    System.out.println("0 - Para sair:");
    System.out.println("(Digite o número e aperte Enter)");
    System.out.println("----------------------------------");
  }
  /* create -> Cria um arquivo sequencial para os dados da base de dados escolhida.
  */
  void create(){
    try{
    RandomAccessFile raf = new RandomAccessFile("baseDados.db","rw");
    File arq = new File("trading-cardsMODIFIED.csv");
    int id = 0;
    Scanner sc2 = new Scanner(arq);
    String line = sc2.nextLine();//Primeira linha da base de dados não possui dados,é o cabeçalho
    raf.seek(4);
    while(sc2.hasNextLine()){
            line = sc2.nextLine();
            String[] informacoes = new String[14];
            int count = 0;
            int size = line.length();
            boolean specialCharCount = true;
            String temp = "";
            //Le as informações de cada linha e as separa a quase cada virgula
            for(int infoCount = 0;infoCount<14 && count<size;count++){
               if(specialCharCount && line.charAt(count)==','){
                   informacoes[infoCount++] = temp;
                   temp = "";
                   }else{
                     temp+=line.charAt(count);
                   }
                    if(line.charAt(count)=='"'){
                        specialCharCount = !specialCharCount;
                    }
            }
            informacoes[13] = temp;//Não há virgula depois da última informação,logo o loop não pega
            
            if((informacoes[0].contains("Pokémon")||informacoes[0].contains("MEGA"))&& !informacoes[0].contains("Trainer")){
                PkmCarta cartinha = new PkmCarta(informacoes,id);
                byte[] vector = cartinha.paraByteArray();
                raf.writeInt(id);
                raf.writeByte(0);//Qualquer valor diferente de 0 indica lápide
                raf.writeByte(12);//12 é o simbolo para indicar que o vetor de bytes seguintes é de uma carta pokemon
                raf.writeInt(vector.length);
                raf.write(vector);
            }else if(informacoes[0].contains("Energy")){
                EnergiaCarta cartinha = new EnergiaCarta(informacoes,id);
                byte[] vector = cartinha.paraByteArray();
                raf.writeInt(id);
                raf.writeByte(0);
                raf.writeByte(24);//24 é o simbolo para indicar que o vetor de bytes seguintes é de uma carta de energia
                raf.writeInt(vector.length);
                raf.write(vector);
            }else{
                TreinadorCarta cartinha = new TreinadorCarta(informacoes,id);
                byte[] vector = cartinha.paraByteArray();
                raf.writeInt(id);
                raf.writeByte(0);
                raf.writeByte(42);//42 é o simbolo para indicar que o vetor de bytes seguintes é de uma carta de treinador
                raf.writeInt(vector.length);
                raf.write(vector);
            }
            long temp2 = raf.getFilePointer();
            raf.seek(0);
            raf.writeInt(id);//Atualizar cabeçalho a cada passada,se houver algum erro durante
            raf.seek(temp2);//a criação,saberemos mais ou menos qual registro deu problema
            id++;
        }
        raf.close();
        sc2.close();
      }catch(Exception e){
      e.printStackTrace();
    }
  }
  /* read -> percorre o arquivo sequencial,procurando um registro com id indicado para ser lido.
  * @param (int id) id indicado
  */
  void read(int id){
    try{
      if(id > -1){
        boolean meet = false;
        RandomAccessFile raf = new RandomAccessFile("baseDados.db","rw");
        raf.seek(4);//Começar a depois do cabeçalho
        long size = raf.length();
        while(! meet && raf.getFilePointer()!=size){
          int id_object =  raf.readInt();
          if(id_object == id){
            meet = true;
            if(0 == raf.readByte()){
              byte check = raf.readByte();//checar o tipo da carta
              switch(check){
              case 12:
              case 24:
              case 42: { 
                //Ler array com dados do registro
                int length = raf.readInt();
                byte [] array = new byte [length];
                raf.read(array);

                if(check == 24){
                   EnergiaCarta carta = new EnergiaCarta(array,id_object);
                   carta.mostrar();
                }else if(check == 12){
                   PkmCarta carta = new PkmCarta(array,id_object); 
                   carta.mostrar();
                }else{
                   TreinadorCarta carta = new TreinadorCarta(array,id_object);
                   carta.mostrar();
                }
              
              }  break;
              default : System.out.println("Registro inválido");
            }
            }else{
              System.out.println("ID inválido");
            }
            
          } else {
            raf.readByte();
            raf.readByte();
            int length_register = raf.readInt();
            long file_position = raf.getFilePointer();
            raf.seek(file_position + length_register);//Pular registro atual
          }

        }

        raf.close();
        
      }
    }catch (Exception io){
      System.out.println(io.getMessage());
    }
  }
  /* update -> percorre o arquivo sequencial,procurando um registro com id indicado para ser atualizado.
  * @param (int id) id indicado
  * @param (Scanner userI) scanner para ser jogado para a função modificar
  */
  void update(int id,Scanner userI){
    try{
      if(id > -1){
        boolean meet = false;
        RandomAccessFile raf = new RandomAccessFile("baseDados.db","rw");
        raf.seek(0);//Começar do cabeçalho e ler ele,caso precisemos de um novo registro
        int last_id = raf.readInt();
        long size = raf.length();
        while(! meet && raf.getFilePointer()!=size){
          int id_object =  raf.readInt();
          if(id_object == id){
            meet = true;
            if(0 == raf.readByte()){
            byte check = raf.readByte();
            switch(check){
              case 12:
              case 24:
              case 42:{
                 //Ler array com dados do registro
                int length = raf.readInt();
                byte [] array = new byte [length];
                long posicao = raf.getFilePointer();//Pegar posição atual caso possamos aproveitar o registro,ou para marcar lápide
                raf.read(array);

                if(check == 24){
                  EnergiaCarta carta = new EnergiaCarta(array,id_object);
                  byte[] arr2 = carta.modificar(userI);
                  if(arr2.length <=length){
                    raf.seek(posicao);
                    raf.write(arr2);
                  }else{
                    int idNew = last_id + 1;//Pegar ultimo id usado no cabeçalho +1
                    raf.seek(0);
                    raf.writeInt(idNew);
                    raf.seek(raf.length() -1);raf.read();//Ir até o final do arquivo
                    //Escrever novo registro porque o espaço antigo não pode ser reaproveitado
                    raf.writeInt(idNew);
                    raf.writeByte(0);
                    raf.writeByte(24);
                    raf.writeInt(arr2.length);
                    raf.write(arr2);
                    //Marcar lápide antiga
                    raf.seek(posicao - 6);
                    raf.writeByte(13);
                  }
                }else if(check == 12){
                  PkmCarta carta = new PkmCarta(array,id_object); 
                  byte[] arr2 = carta.modificar(userI);
                  if(arr2.length <=length){
                    raf.seek(posicao);
                    raf.write(arr2);
                  }else{
                    int idNew = last_id + 1;//Pegar ultimo id usado no cabeçalho +1
                    raf.seek(0);
                    raf.writeInt(idNew);
                    raf.seek(raf.length() -1);raf.read();//Ir até o final do arquivo
                    //Escrever novo registro porque o espaço antigo não pode ser reaproveitado
                    raf.writeInt(idNew);
                    raf.writeByte(0);
                    raf.writeByte(12);
                    raf.writeInt(arr2.length);
                    raf.write(arr2);
                    //Marcar lápide antiga
                    raf.seek(posicao - 6);
                    raf.writeByte(13);
                  }
                }else{
                  TreinadorCarta carta = new TreinadorCarta(array,id_object);
                  byte[] arr2 = carta.modificar(userI);
                  if(arr2.length <=length){
                    raf.seek(posicao);
                    raf.write(arr2);
                  }else{
                    int idNew = last_id + 1;//Pegar ultimo id usado no cabeçalho +1
                    raf.seek(0);
                    raf.writeInt(idNew);
                    raf.seek(raf.length() -1);raf.read();//Ir até o final do arquivo
                    //Escrever novo registro porque o espaço antigo não pode ser reaproveitado
                    raf.writeInt(idNew);
                    raf.writeByte(0);
                    raf.writeByte(42);
                    raf.writeInt(arr2.length);
                    raf.write(arr2);
                    //Marcar lápide antiga
                    raf.seek(posicao - 6);
                    raf.writeChar(13);
                  }
                }
              } break;
            
              default : System.out.println("Registro inválido");
            }
          }else{
        System.out.println("ID inválido");
      }
        }else{
            raf.readByte();
            raf.readByte();
            int length_register = raf.readInt();
            long file_position = raf.getFilePointer();
            raf.seek(file_position + length_register);//Pular registro atual
          } 
      }
      raf.close();
    }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
/* read -> percorre o arquivo sequencial,procurando um registro com id indicado para ser deletado.
* @param (int id) id indicado
*/
  void delete(int id){
    try{
      if(id > -1){
        boolean meet = false;
        RandomAccessFile raf = new RandomAccessFile("baseDados.db","rw");  
        raf.seek(4);
        long size = raf.length();
        while(! meet && raf.getFilePointer()!=size){
  
          int object_id = raf.readInt();
          if(object_id == id){
            meet = true;
            raf.writeByte(13);
          }else{
            raf.readByte();
            raf.readByte();
            int length_register = raf.readInt();
            long file_position = raf.getFilePointer();
            raf.seek(file_position + length_register);//Pular registro atual 
          }
        
        }

        if(meet){
          System.out.println("Deletado com Sucesso");
        }else{
          System.out.println("Registro não encontrado :(");
        }
        raf.close();
      }else{
        System.out.println("ID Inválido");
      }
    }catch(Exception io){
      System.out.println(io.getMessage());
      io.printStackTrace();
    }
  }

  void ordenar(int option){
    if(option == 1){

    }else if(option == 2 ){

    }else if(option == 3){

    }else{
      System.out.println("Opção Inválida");
    }
  }
}
