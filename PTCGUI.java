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
          if(option != 4){
            ordenar(option);
          }
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
    System.out.println("1 - Para criar[(C)reate] a base de dados(BD)(Demora alguns segundos):");
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
                byte[] array = new byte [length];
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
/* delete -> percorre o arquivo sequencial,procurando um registro com id indicado para ser deletado.
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
  /* ordenar -> Chama o metodo adequado de ordenação,de acordo com a escolha do usuário
  * @param (int option) escolha do usuário
  */
  void ordenar(int option){
    if(option == 1){

    }else if(option == 2 ){

    }else if(option == 3){
      intercalacaoSelectSubstitute();
    }else{
      System.out.println("Opção Inválida");
    }
  }
  /* sortCarta -> De acordo com os seguintes critérios,na ordem a seguir,ordena as duas
  * cartas argumento retornando true se a primeira for "menor" que a segunda,false caso
  * contrário.
  * Data lançamento -> Coleção contem a palavra Promo,Alternate ou POP -> Numeração -> Nome Carta
  * @param (Carta card1) Carta 1 a ordenar
  * @param (Carta card2) Carta 2 a ordenar
  * @return (boolean) Resposta se o primeiro card é "menor" que o segundo 
  */
  boolean sortCarta(Carta card1,Carta card2){
    boolean resp = true;
    int igualFlag = 0;//Checa quantas vezes as comparações deram igual
    //Checar data
    if(card1.dataLancamento.compareTo(card2.dataLancamento)>0 ){
      resp = false;
    }else if(card1.dataLancamento.compareTo(card2.dataLancamento)==0){
      igualFlag++;
    }
    //Checagem coleção,se a última checagem foi igual
    if(igualFlag == 1){
      if(card1.colecao.compareTo(card2.colecao)==0){
        igualFlag++;
      }else if(card1.colecao.contains("Promo") ||card1.colecao.contains("A Alternate") || card1.colecao.contains("POP")){ 
        //Se a carta 1 conter alguma das palavras acima,mas 2 não,indica que 1 vem de uma coleção
        //promocional,e quero que elas venham primeiro que as coleções normais de uma geração
        //Como já inicializo resp como true,nada a fazer aqui
      }else if(card2.colecao.contains("Promo") ||card2.colecao.contains("A Alternate") || card2.colecao.contains("POP")){
        resp = false;
      }else if(card1.colecao.contains("Gallery")){
      //Nada a fazer aqui
      }else if(card2.colecao.contains("Gallery")){
        resp = false;
      }else if(card1.colecao.contains("Vault")){
      //Nada a fazer aqui
      }else if(card2.colecao.contains("Vault")){
        resp = false;
      }else if(card1.colecao.contains("Classic Collection")){
      //Nada a fazer aqui
      }else if(card2.colecao.contains("Classic Collection")){
        resp = false;
      }else{
        //Pela lógica do jogo,não deveria chegar aqui,mas caso chegue eu preciso saber para corrigir depois
        System.out.println("Erro ordenação:Não foi possível checar se coleção é promo.");
        System.out.println("Colecao1: "+card1.colecao);
        System.out.println("Colecao1: "+card2.colecao);
      }
    }
    //Checagem numeração,se a última checagem foi igual
    if(igualFlag == 2){
      if(card1.numeracao>card2.numeracao){
        resp = false;
      }else if(card1.numeracao==card2.numeracao){
       //Existem algumas poucas coleções que a numeração não é feita com números,e eu coloquei 0 como a numeração
       //de todas essas cartas.Nesse caso,vou ordenar pelo nome da carta.
       igualFlag++;
      }
    }
    //Checagem nome carta,se a última checagem foi igual
    if(igualFlag == 3 && card1.nome.compareTo(card2.nome)>0){
      resp = false;
    }
    return resp;
  }
  /*intercalacaoSelectSubstitute -> Faz a ordenação do arquivo de vetor de bytes,caso ele exista,
  * por meio da intercalação com seleção por substituição.
  */
  void intercalacaoSelectSubstitute(){
    try{
      RandomAccessFile raf = new RandomAccessFile("baseDados.db","rw");
      RandomAccessFile rafTemp1 = new RandomAccessFile("arquivoTemp1.temp","rw");
      RandomAccessFile rafTemp2 = new RandomAccessFile("arquivoTemp2.temp","rw");
      RandomAccessFile rafTemp3 = new RandomAccessFile("arquivoTemp3.temp","rw");
      RandomAccessFile rafTemp4 = new RandomAccessFile("arquivoTemp4.temp","rw");
      raf.seek(4);//Começar a depois do cabeçalho
      long size = raf.length();
      Carta[] heapCarta = new Carta[31];//heap de cartas
      int[][] heapId = new int[31][3];//heap dos ids das cartas do heap da linha acima,e da ordem na ordenação
      int count = 0;//Contador de conteudo nos heaps
      int order = 0;//Ordem atual no heap
      while(raf.getFilePointer()!=size || count>0){
        //Checar lápide e se já lemos o arquivo todo
        if(raf.getFilePointer()!=size){
          int idAtual = raf.readInt();
          if(0 != raf.readByte()){
            //Se registro inválido,pular ele
            raf.readByte();
            int size2 = raf.readInt();
            raf.seek(raf.getFilePointer()+size2);
          }else{
          byte checkType = raf.readByte();//Checar tipo carta
          int size2 = raf.readInt();
          byte[] arr = new byte[size2];
          raf.read(arr);
          switch(checkType){
            case 12:{
              PkmCarta cartinha = new PkmCarta(arr,idAtual);
              if(count<31){
                heapCarta[count] = cartinha;
                heapId[count][0] = idAtual;
                heapId[count][1] = order;
                heapId[count][2] = 12;//Colocar aqui qual tipo de carta é
                count++;
              }else{
                ordenarHeap(heapCarta,heapId,count);
                if(heapId[0][1]%2==0){
                  if(heapId[0][1]!=order){
                    order++;
                    rafTemp2.writeInt(-1);//Estou usando -1 no lugar de um id para separar os segmentos,ja que pelo o que entendi
                    //não há como controlar o tamanho deles neste tipo de intercalação
                  }
                  //escrever registro no arquivo temporario
                  rafTemp1.writeInt(heapId[0][0]);
                  //como sei que o registro é válido,não vou colocar a lápide no arquivo temp,
                  //mas ela volta quando terminarmos a ordenação e formos reescrever o arquivo .db
                  byte[] arrSorted;
                  //Descobrir qual o tipo de carta que esta no topo do heap
                  if(heapId[0][2] == 12){
                    rafTemp1.writeByte(12);
                    PkmCarta gamb = (PkmCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 24){
                    rafTemp1.writeByte(24);
                    EnergiaCarta gamb = (EnergiaCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 42){
                    rafTemp1.writeByte(42);
                    TreinadorCarta gamb = (TreinadorCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else{
                    System.out.println("Error Code:1");
                    arrSorted = new byte[]{1,2,3};
                  }
                  rafTemp1.writeInt(arrSorted.length);
                  rafTemp1.write(arrSorted);
                }else{
                  if(heapId[0][1]!=order){
                    order++;
                    rafTemp1.writeInt(-1);
                  }
                  rafTemp2.writeInt(heapId[0][0]);
                  byte[] arrSorted;
                  if(heapId[0][2] == 12){
                    rafTemp2.writeByte(12);
                    PkmCarta gamb = (PkmCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 24){
                    rafTemp2.writeByte(24);
                    EnergiaCarta gamb = (EnergiaCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 42){
                    rafTemp2.writeByte(42);
                    TreinadorCarta gamb = (TreinadorCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else{
                    System.out.println("Error Code:2");
                    arrSorted = new byte[]{1,2,3};
                  }
                  rafTemp2.writeInt(arrSorted.length);
                  rafTemp2.write(arrSorted);
                }
                if(sortCarta(cartinha,heapCarta[0])){
                  //Se a carta que vamos inserir for menor que o último tirado
                  //Devemos dar a ela uma ordem maior antes de inserir
                heapId[0][1] = order+1;
                }else{
                  heapId[0][1] = order;
                }
                heapCarta[0] = new PkmCarta(cartinha);
                heapId[0][0] = idAtual;
                heapId[0][2] = 12; 
              }
            }
            break;
            case 24:
            {
              EnergiaCarta cartinha = new EnergiaCarta(arr,idAtual);
              if(count<31){
                heapCarta[count] = cartinha;
                heapId[count][0] = idAtual;
                heapId[count][1] = order;
                heapId[count][2] = 24;
                count++;
              }else{
                ordenarHeap(heapCarta,heapId,count);
              if(heapId[0][1]%2==0){
                  if(heapId[0][1]!=order){
                    order++;
                    rafTemp2.writeInt(-1);
                  }
                  
                  rafTemp1.writeInt(heapId[0][0]);
                  byte[] arrSorted;
                  if(heapId[0][2] == 12){
                    rafTemp1.writeByte(12);
                    PkmCarta gamb = (PkmCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 24){
                    rafTemp1.writeByte(24);
                    EnergiaCarta gamb = (EnergiaCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 42){
                    rafTemp1.writeByte(42);
                    TreinadorCarta gamb = (TreinadorCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else{
                    System.out.println("Error Code:3");
                    arrSorted = new byte[]{1,2,3};
                  }
                  rafTemp1.writeInt(arrSorted.length);
                  rafTemp1.write(arrSorted);
                }else{
                  if(heapId[0][1]!=order){
                    order++;
                    rafTemp1.writeInt(-1);
                  }
                  rafTemp2.writeInt(heapId[0][0]);
                  byte[] arrSorted;
                  if(heapId[0][2] == 12){
                    rafTemp2.writeByte(12);
                    PkmCarta gamb = (PkmCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 24){
                    rafTemp2.writeByte(24);
                    EnergiaCarta gamb = (EnergiaCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 42){
                    rafTemp2.writeByte(42);
                    TreinadorCarta gamb = (TreinadorCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else{
                    System.out.println("Error Code:4");
                    arrSorted = new byte[]{1,2,3};
                  }
                  rafTemp2.writeInt(arrSorted.length);
                  rafTemp2.write(arrSorted);
                }
              if(sortCarta(cartinha,heapCarta[0])){
                heapId[0][1] = order +1;
                }else{
                  heapId[0][1] = order;
                }
                heapCarta[0] = new EnergiaCarta(cartinha);
                heapId[0][0] = idAtual;
                heapId[0][2] = 24; 
              }
            }
            break;
            case 42:
            {
              TreinadorCarta cartinha = new TreinadorCarta(arr,idAtual);
              if(count<31){
                heapCarta[count] = cartinha;
                heapId[count][0] = idAtual;
                heapId[count][1] = order;
                heapId[count][2] = 42;
                count++;
                ordenarHeap(heapCarta,heapId,count);
              }else{
                ordenarHeap(heapCarta,heapId,count);
              if(heapId[0][1]%2==0){
                  if(heapId[0][1]!=order){
                    order++;
                    rafTemp2.writeInt(-1);
                  }
                  rafTemp1.writeInt(heapId[0][0]);
                  byte[] arrSorted;
                  if(heapId[0][2] == 12){
                    rafTemp1.writeByte(12);
                    PkmCarta gamb = (PkmCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 24){
                    rafTemp1.writeByte(24);
                    EnergiaCarta gamb = (EnergiaCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 42){
                    rafTemp1.writeByte(42);
                    TreinadorCarta gamb = (TreinadorCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else{
                    System.out.println("Error Code:5");
                    arrSorted = new byte[]{1,2,3};
                  }
                  rafTemp1.writeInt(arrSorted.length);
                  rafTemp1.write(arrSorted);
                }else{
                  if(heapId[0][1]!=order){
                    order++;
                    rafTemp1.writeInt(-1);
                  }
                  rafTemp2.writeInt(heapId[0][0]);
                 byte[] arrSorted;
                  if(heapId[0][2] == 12){
                    rafTemp2.writeByte(12);
                    PkmCarta gamb = (PkmCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 24){
                    rafTemp2.writeByte(24);
                    EnergiaCarta gamb = (EnergiaCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 42){
                    rafTemp2.writeByte(42);
                    TreinadorCarta gamb = (TreinadorCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else{
                    System.out.println("Error Code:6");
                    arrSorted = new byte[]{1,2,3};
                  }
                  rafTemp2.writeInt(arrSorted.length);
                  rafTemp2.write(arrSorted);
                }
             if(sortCarta(cartinha,heapCarta[0])){
                heapId[0][1] = order +1;
                }else{
                  heapId[0][1] = order;
                }
                heapCarta[0] = new TreinadorCarta(cartinha);
                heapId[0][0] = idAtual;
                heapId[0][2] = 42; 
              }
            }
            break;
            default:
              System.out.println("Erro ao ler o tipo da carta(Pokemon,Energia ou treinador).");
            break;
          }
          }
        }else if(raf.getFilePointer() == size){
          //Mesmo apos ler o arquivo todo,sobrarão alguns registros no heap
          if(count >1){
          ordenarHeap(heapCarta,heapId,count);
          }else{
            int v = 0;
            for(int reg = 1;v== 0 &&reg<31;reg++){
              if(heapCarta[reg]!= null){
                v = reg;
              }
            }
          switch(heapId[v][2]){
              case 12:{
                PkmCarta temp1 = (PkmCarta)heapCarta[v];
                heapCarta[v] = null;
                heapCarta[0] = new PkmCarta(temp1);
              }
              break;
              case 24:
              {
                EnergiaCarta temp1 = (EnergiaCarta)heapCarta[v];
                heapCarta[v] = null;
                heapCarta[0] = new EnergiaCarta(temp1);
              }
              break;
              case 42:
              {
                TreinadorCarta temp1 = (TreinadorCarta)heapCarta[v];
                heapCarta[v] = null;
                heapCarta[0] = new TreinadorCarta(temp1);
              }
              break;
        }
        int tempI0 = heapId[0][0];
        int tempI1 = heapId[0][1];
        int tempI2 = heapId[0][2];
        heapId[0][0] = heapId[v][0];
        heapId[0][1] = heapId[v][1];
        heapId[0][2] = heapId[v][2];
        heapId[v][0] = tempI0;
        heapId[v][1] = tempI1;
        heapId[v][2] = tempI2;
          }
          if(heapId[0][1]%2==0){
                  if(heapId[0][1]!=order){
                    order++;
                    rafTemp2.writeInt(-1);
                  }
                  rafTemp1.writeInt(heapId[0][0]);
                  byte[] arrSorted;
                  if(heapId[0][2] == 12){
                    rafTemp1.writeByte(12);
                    PkmCarta gamb = (PkmCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 24){
                    rafTemp1.writeByte(24);
                    EnergiaCarta gamb = (EnergiaCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 42){
                    rafTemp1.writeByte(42);
                    TreinadorCarta gamb = (TreinadorCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else{
                    System.out.println("Error Code:7");
                    arrSorted = new byte[]{1,2,3};
                  }
                  rafTemp1.writeInt(arrSorted.length);
                  rafTemp1.write(arrSorted);
                }else{
                  if(heapId[0][1]!=order){
                    order++;
                    rafTemp1.writeInt(-1);
                  }
                  rafTemp2.writeInt(heapId[0][0]);
                 byte[] arrSorted;
                  if(heapId[0][2] == 12){
                    rafTemp2.writeByte(12);
                    PkmCarta gamb = (PkmCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 24){
                    rafTemp2.writeByte(24);
                    EnergiaCarta gamb = (EnergiaCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else if(heapId[0][2] == 42){
                    rafTemp2.writeByte(42);
                    TreinadorCarta gamb = (TreinadorCarta) heapCarta[0];
                    arrSorted = gamb.paraByteArray();
                  }else{
                    System.out.println("Error Code:8 +" + count);
                    arrSorted = new byte[]{1,2,3};
                  }
                  rafTemp2.writeInt(arrSorted.length);
                  rafTemp2.write(arrSorted);
                }
                heapCarta[0] = null;
                heapId[0][0] = -1;
                heapId[0][1] = -1;
                heapId[0][2] = -1;
          count--;
        }
      }
      //Escrever delimitadores finais,caso não existam
      rafTemp1.seek(rafTemp1.getFilePointer() - 4);
      if(!(rafTemp1.readInt()==-1)){
      rafTemp1.writeInt(-1);
      }
      rafTemp2.seek(rafTemp2.getFilePointer() - 4);
      if(!(rafTemp2.readInt()==-1)){
      rafTemp2.writeInt(-1);
      }
      //Começar a intercalar
      intercalarSelectSub(rafTemp1,rafTemp2,rafTemp3,rafTemp4,raf);
      //Achar arquivo onde intercalação parou pelo seu tamanho
      
    int parou = 1;
    long parouTam = rafTemp1.length();
    if(parouTam < rafTemp2.length()){
      parou = 2;
      parouTam = rafTemp2.length();
    }
    if(parouTam < rafTemp3.length()){
      parou = 3;
      parouTam = rafTemp3.length();
    }
    if(parouTam < rafTemp4.length()){
      parou = 4;
      parouTam = rafTemp4.length();
    }
    raf.seek(0);
    int idMax = raf.readInt();
    raf.setLength(0);//Apagar db original
    raf.writeInt(idMax);
    switch(parou){
      case 1:{
      rafTemp1.seek(0);
      while(rafTemp1.getFilePointer()<parouTam - 4){
        int idd = rafTemp1.readInt();
        raf.writeInt(idd);
        raf.writeByte(0);//Todos os registros vindo da intercalação são válidos
        byte gamb69 = rafTemp1.readByte();
        raf.writeByte(gamb69);
        int sizeG = rafTemp1.readInt();
        raf.writeInt(sizeG);
        byte[] arr = new byte[sizeG];
        rafTemp1.read(arr);
        switch(gamb69){
          case 12:
            {
              PkmCarta cartinha = new PkmCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          case 24:
            {
              EnergiaCarta cartinha = new EnergiaCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          case 42:
            {
              TreinadorCarta cartinha = new TreinadorCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          default:
            System.out.println("ERRROOOOR");
            raf.write(arr);
          break;
        }
       
      }
      }
      break;
      case 2:
      {
      rafTemp2.seek(0);
      while(rafTemp2.getFilePointer()<parouTam - 4){
        int idd = rafTemp2.readInt();
        raf.writeInt(idd);
        raf.writeByte(0);//Todos os registros vindo da intercalação são válidos
        byte gamb69 = rafTemp2.readByte();
        raf.writeByte(gamb69);
        int sizeG = rafTemp2.readInt();
        raf.writeInt(sizeG);
        byte[] arr = new byte[sizeG];
        rafTemp2.read(arr);
        switch(gamb69){
          case 12:
            {
              PkmCarta cartinha = new PkmCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          case 24:
            {
              EnergiaCarta cartinha = new EnergiaCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          case 42:
            {
              TreinadorCarta cartinha = new TreinadorCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          default:
            System.out.println("ERRROOOOR");
            raf.write(arr);
          break;
        }
       
      }
      }
      break;
      case 3:
      {
      rafTemp3.seek(0);
      while(rafTemp3.getFilePointer()<parouTam - 4){
        int idd = rafTemp3.readInt();
        raf.writeInt(idd);
        raf.writeByte(0);//Todos os registros vindo da intercalação são válidos
        byte gamb69 = rafTemp3.readByte();
        raf.writeByte(gamb69);
        int sizeG = rafTemp3.readInt();
        raf.writeInt(sizeG);
        byte[] arr = new byte[sizeG];
        rafTemp3.read(arr);
        switch(gamb69){
          case 12:
            {
              PkmCarta cartinha = new PkmCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          case 24:
            {
              EnergiaCarta cartinha = new EnergiaCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          case 42:
            {
              TreinadorCarta cartinha = new TreinadorCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          default:
            System.out.println("ERRROOOOR");
            raf.write(arr);
          break;
        }
       
      }
      }
      break;
      case 4:
      {
      rafTemp1.seek(0);
      while(rafTemp4.getFilePointer()<parouTam - 4){
        int idd = rafTemp4.readInt();
        raf.writeInt(idd);
        raf.writeByte(0);//Todos os registros vindo da intercalação são válidos
        byte gamb69 = rafTemp4.readByte();
        raf.writeByte(gamb69);
        int sizeG = rafTemp4.readInt();
        raf.writeInt(sizeG);
        byte[] arr = new byte[sizeG];
        rafTemp4.read(arr);
        switch(gamb69){
          case 12:
            {
              PkmCarta cartinha = new PkmCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          case 24:
            {
              EnergiaCarta cartinha = new EnergiaCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          case 42:
            {
              TreinadorCarta cartinha = new TreinadorCarta(arr,idd);
              raf.write(cartinha.paraByteArray());
            }
          break;
          default:
            System.out.println("ERRROOOOR");
            raf.write(arr);
          break;
        }
       
      }
      }
      break;
    }
      //Fechar arquivos e deletar os temporarios
      raf.close();
      rafTemp1.close();
      rafTemp2.close();
      rafTemp3.close();
      rafTemp4.close();
      
      File arq1 = new File("arquivoTemp1.temp");
      arq1.delete();
      File arq2 = new File("arquivoTemp2.temp");
      arq2.delete();
      File arq3 = new File("arquivoTemp3.temp");
      arq3.delete();
      File arq4 = new File("arquivoTemp4.temp");
      arq4.delete();
      
    }catch(Exception e){
      e.printStackTrace();
    }

  }
  /* buildHeap -> Constroi um max heap dos arrays argumentos
  *
  */
  void buildHeap(Carta[] arrC,int[][] arrI,int s,int tam){
      int maior = s;
      int esq = s*2 +1;
      int dir = s*2 +2;
      if(arrC[s] != null){
        if(esq < tam && arrC[esq]!= null && sortCarta(arrC[maior],arrC[esq])){
          maior = esq;
        }else if(esq < tam && arrC[esq] == null){
          maior = esq;
        }
        if(dir < tam && arrC[dir]!= null && sortCarta(arrC[maior],arrC[dir])){
          maior = dir;
        }else if(dir < tam && arrC[dir] == null && arrC[esq]!= null){
          maior = dir;
        }
      }
      if(maior != s){
        if(arrC[maior]!=null){
          switch(arrI[maior][2]){
          case 12:
          {
            switch(arrI[s][2]){
          case 12:
          {
            PkmCarta temp1 = (PkmCarta)arrC[maior];
            arrC[maior] = new PkmCarta((PkmCarta)arrC[s]);
            arrC[s] = new PkmCarta(temp1);
          }
          break;
          case 24:
          {
            PkmCarta temp1 = (PkmCarta)arrC[maior];
            arrC[maior] = new EnergiaCarta((EnergiaCarta)arrC[s]);
            arrC[s] = new PkmCarta(temp1);
          }
          break; 
          case 42:
          {
            PkmCarta temp1 = (PkmCarta)arrC[maior];
            arrC[maior] = new TreinadorCarta((TreinadorCarta)arrC[s]);
            arrC[s] = new PkmCarta(temp1);
          }
          break;
        }
          }
          break;
          case 24:
          {
            switch(arrI[s][2]){
          case 12:
          {
            EnergiaCarta temp1 = (EnergiaCarta)arrC[maior];
            arrC[maior] = new PkmCarta((PkmCarta)arrC[s]);
            arrC[s] = new EnergiaCarta(temp1);
          }
          break;
          case 24:
          {
            EnergiaCarta temp1 = (EnergiaCarta)arrC[maior];
            arrC[maior] = new EnergiaCarta((EnergiaCarta)arrC[s]);
            arrC[s] = new EnergiaCarta(temp1);
          }
          break;
          case 42:
          {
            EnergiaCarta temp1 = (EnergiaCarta)arrC[maior];
            arrC[maior] = new TreinadorCarta((TreinadorCarta)arrC[s]);
            arrC[s] = new EnergiaCarta(temp1);
          }
          break;
        }
          }
          break;
          case 42:
          {
            switch(arrI[s][2]){
          case 12:
          {
            TreinadorCarta temp1 = (TreinadorCarta)arrC[maior];
            arrC[maior] = new PkmCarta((PkmCarta)arrC[s]);
            arrC[s] = new TreinadorCarta(temp1);
          }
          break;
          case 24:
          {
            TreinadorCarta temp1 = (TreinadorCarta)arrC[maior];
            arrC[maior] = new EnergiaCarta((EnergiaCarta)arrC[s]);
            arrC[s] = new TreinadorCarta(temp1); 
          }
          break;
          case 42:
          {
            TreinadorCarta temp1 = (TreinadorCarta)arrC[maior];
            arrC[maior] = new TreinadorCarta((TreinadorCarta)arrC[s]);
            arrC[s] = new TreinadorCarta(temp1);
          }
          break;
        }
          }
          break;
        }
        
        //trocar pai e filho no array de ids
        int tempI0 = arrI[maior][0];
        int tempI1 = arrI[maior][1];
        int tempI2 = arrI[maior][2];
        arrI[maior][0] = arrI[s][0];
        arrI[maior][1] = arrI[s][1];
        arrI[maior][2] = arrI[s][2];
        arrI[s][0] = tempI0;
        arrI[s][1] = tempI1;
        arrI[s][2] = tempI2;
        }else{
          switch(arrI[0][2]){
              case 12:{
                PkmCarta temp1 = (PkmCarta)arrC[0];
                arrC[0] = null;
                arrC[maior] = new PkmCarta(temp1);
              }
              break;
              case 24:
              {
                EnergiaCarta temp1 = (EnergiaCarta)arrC[0];
                arrC[0] = null;
                arrC[maior] = new EnergiaCarta(temp1);
              }
              break;
              case 42:
              {
                TreinadorCarta temp1 = (TreinadorCarta)arrC[0];
                arrC[0] = null;
                arrC[maior] = new TreinadorCarta(temp1);
              }
              break;
        }
        int tempI0 = arrI[0][0];
        int tempI1 = arrI[0][1];
        int tempI2 = arrI[0][2];
        arrI[0][0] = arrI[maior][0];
        arrI[0][1] = arrI[maior][1];
        arrI[0][2] = arrI[maior][2];
        arrI[maior][0] = tempI0;
        arrI[maior][1] = tempI1;
        arrI[maior][2] = tempI2;
        }  
        buildHeap(arrC,arrI,maior,tam);
      }
  }
  /* ordenarHeap -> ordena os heaps argumento,de acordo com o metodo
  * @param (Carta[] arrC) heap de cartas
  * @param (int[][] arrI) heap de ids
  */
  void ordenarHeap(Carta[] arrC,int[][] arrI,int N){
    for(int v = N/2 - 1;v>=0;v--){
      buildHeap(arrC,arrI,v,N);
    }
     for(int v = N - 1;v >= 0;v--){
      if(arrC[v] == null){
        if(arrC[0]!= null){
          switch(arrI[0][2]){
              case 12:{
                PkmCarta temp1 = (PkmCarta)arrC[0];
                arrC[0] = null;
                arrC[v] = new PkmCarta(temp1);
              }
              break;
              case 24:
              {
                EnergiaCarta temp1 = (EnergiaCarta)arrC[0];
                arrC[0] = null;
                arrC[v] = new EnergiaCarta(temp1);
              }
              break;
              case 42:
              {
                TreinadorCarta temp1 = (TreinadorCarta)arrC[0];
                arrC[0] = null;
                arrC[v] = new TreinadorCarta(temp1);
              }
              break;
        }
        int tempI0 = arrI[0][0];
        int tempI1 = arrI[0][1];
        int tempI2 = arrI[0][2];
        arrI[0][0] = arrI[v][0];
        arrI[0][1] = arrI[v][1];
        arrI[0][2] = arrI[v][2];
        arrI[v][0] = tempI0;
        arrI[v][1] = tempI1;
        arrI[v][2] = tempI2;
        }
      }else{  
        if(arrC[0]!=null){
        switch(arrI[0][2]){
          case 12:
          {
            switch(arrI[v][2]){
          case 12:
          {
            PkmCarta temp1 = (PkmCarta)arrC[0];
            arrC[0] = new PkmCarta((PkmCarta)arrC[v]);
            arrC[v] = new PkmCarta(temp1);
          }
          break;
          case 24:
          {
            PkmCarta temp1 = (PkmCarta)arrC[0];
            arrC[0] = new EnergiaCarta((EnergiaCarta)arrC[v]);
            arrC[v] = new PkmCarta(temp1);
          }
          break; 
          case 42:
          {
            PkmCarta temp1 = (PkmCarta)arrC[0];
            arrC[0] = new TreinadorCarta((TreinadorCarta)arrC[v]);
            arrC[v] = new PkmCarta(temp1);
          }
          break;
        }
          }
          break;
          case 24:
          {
            switch(arrI[v][2]){
          case 12:
          {
            EnergiaCarta temp1 = (EnergiaCarta)arrC[0];
            arrC[0] = new PkmCarta((PkmCarta)arrC[v]);
            arrC[v] = new EnergiaCarta(temp1);
          }
          break;
          case 24:
          {
            EnergiaCarta temp1 = (EnergiaCarta)arrC[0];
            arrC[0] = new EnergiaCarta((EnergiaCarta)arrC[v]);
            arrC[v] = new EnergiaCarta(temp1);
          }
          break;
          case 42:
          {
            EnergiaCarta temp1 = (EnergiaCarta)arrC[0];
            arrC[0] = new TreinadorCarta((TreinadorCarta)arrC[v]);
            arrC[v] = new EnergiaCarta(temp1);
          }
          break;
        }
          }
          break;
          case 42:
          {
            switch(arrI[v][2]){
          case 12:
          {
            TreinadorCarta temp1 = (TreinadorCarta)arrC[0];
            arrC[0] = new PkmCarta((PkmCarta)arrC[v]);
            arrC[v] = new TreinadorCarta(temp1);
          }
          break;
          case 24:
          {
            TreinadorCarta temp1 = (TreinadorCarta)arrC[0];
            arrC[0] = new EnergiaCarta((EnergiaCarta)arrC[v]);
            arrC[v] = new TreinadorCarta(temp1); 
          }
          break;
          case 42:
          {
            TreinadorCarta temp1 = (TreinadorCarta)arrC[0];
            arrC[0] = new TreinadorCarta((TreinadorCarta)arrC[v]);
            arrC[v] = new TreinadorCarta(temp1);
          }
          break;
        }
          }
          break;
        }
        
        //trocar pai e filho no array de ids
        int tempI0 = arrI[0][0];
        int tempI1 = arrI[0][1];
        int tempI2 = arrI[0][2];
        arrI[0][0] = arrI[v][0];
        arrI[0][1] = arrI[v][1];
        arrI[0][2] = arrI[v][2];
        arrI[v][0] = tempI0;
        arrI[v][1] = tempI1;
        arrI[v][2] = tempI2;
     }else{
        switch(arrI[v][2]){
              case 12:{
                PkmCarta temp1 = (PkmCarta)arrC[v];
                arrC[v] = null;
                arrC[0] = new PkmCarta(temp1);
              }
              break;
              case 24:
              {
                EnergiaCarta temp1 = (EnergiaCarta)arrC[v];
                arrC[v] = null;
                arrC[0] = new EnergiaCarta(temp1);
              }
              break;
              case 42:
              {
                TreinadorCarta temp1 = (TreinadorCarta)arrC[v];
                arrC[v] = null;
                arrC[0] = new TreinadorCarta(temp1);
              }
              break;
        }
        int tempI0 = arrI[v][0];
        int tempI1 = arrI[v][1];
        int tempI2 = arrI[v][2];
        arrI[v][0] = arrI[0][0];
        arrI[v][1] = arrI[0][1];
        arrI[v][2] = arrI[0][2];
        arrI[0][0] = tempI0;
        arrI[0][1] = tempI1;
        arrI[0][2] = tempI2;
     }
      }
  buildHeap(arrC,arrI,0,v);
  }
  }
  /* intercalarSelectSub -> faz a intercalação,preparada pela seleção por substituição
  * nos arquivos temporários argumentos e sobrescreve o arquivo .db argumento com o resultado
  * @param (RandomAccessFile temp1) arquivo temporario 1
  * @param (RandomAccessFile temp2) arquivo temporario 2
  * @param (RandomAccessFile temp3) arquivo temporario 3
  * @param (RandomAccessFile temp4) arquivo temporario 4
  * @param (RandomAccessFile db) arquivo a sobrescrever
  */
  void intercalarSelectSub(RandomAccessFile temp1,RandomAccessFile temp2,RandomAccessFile temp3,RandomAccessFile temp4,RandomAccessFile db){
    try{
    boolean Or1234 = true;//Se true,estou lendo dos arquivos 1 e 2 e escrevendo no 3 e 4
    //Se false,o contrário
    do{
      if(Or1234){
        temp1.seek(0);
        temp2.seek(0);
        temp3.seek(0);
        temp4.seek(0);
        //Apagar temp 3 e 4
        temp3.setLength(0);
        temp4.setLength(0);
        long size1 = temp1.length();
        long size2 = temp2.length();
        boolean write34 = true;//Se true,escrevendo em 3.Se false,escrevendo em 4
        //Enquanto não terminamos de ler os arquivos temporarios por inteiro
        while(temp1.getFilePointer()!= size1 || temp2.getFilePointer()!= size2){ 
          boolean temp1End = false,temp2End = false;//Para saber se chegamos ao fim dos segmentos
          //Enquanto não terminamos os dois segmentos escolhidos
          PkmCarta card1 = null,card2 = null;//Placeholders
          EnergiaCarta card3 = null,card4 = null;
          TreinadorCarta card5 = null,card6 = null;
          //Remdiar situação em que só há um arquivo com segmento a ser lido
          if(temp1.getFilePointer()==size1){
            temp1End = true;
          }
          if(temp2.getFilePointer()==size2){
            temp2End = true;
          }
          while(!temp1End || !temp2End){ 
          if(temp1End){
            if(!temp2End){
            int test = temp2.readInt();
            if(test== -1){
              temp2End = true;
            }else{
              if(write34){
                temp3.writeInt(test);
                byte gamb23 = temp2.readByte();
                temp3.writeByte(gamb23);
                int size = temp2.readInt();
                temp3.writeInt(size);
                byte[] arrB = new byte[size];
                temp2.read(arrB);
                temp3.write(writeByteArr(arrB,gamb23));
              }else{
                temp4.writeInt(test);
                byte gamb23 = temp2.readByte();
                temp4.writeByte(gamb23);
                int size = temp2.readInt();
                temp4.writeInt(size);
                byte[] arrB = new byte[size];
                temp2.read(arrB);
                temp4.write(writeByteArr(arrB,gamb23));
              }
            }
            }
          }else{
            if(temp2End){
              int test = temp1.readInt();
            if(test== -1){
              temp1End = true;
            }else{
              if(write34){
                temp3.writeInt(test);
                byte gamb100 = temp1.readByte();
                temp3.writeByte(gamb100);
                int size = temp1.readInt();
                temp3.writeInt(size);
                byte[] arrB = new byte[size];
                temp1.read(arrB);
                temp3.write(writeByteArr(arrB,gamb100));
              }else{
                temp4.writeInt(test);
                byte gamb100 = temp1.readByte();
                temp4.writeByte(gamb100);
                int size = temp1.readInt();
                temp4.writeInt(size);
                byte[] arrB = new byte[size];
                temp1.read(arrB);
                temp4.write(writeByteArr(arrB,gamb100));
              }
            }
            }else{
              int flagCardT1 = 0,flagCardT2 = 0;//Flag para saber qual tipo de carta esta ativa
              int idT1,idT2;
              //Checar se não há carta placeholder que perdeu uma checagem anterior
             if(card1 == null && card3 == null && card5 == null){
                idT1 = temp1.readInt();
                if(idT1 == -1){
                  flagCardT1 = -1;
                  temp1End = true;
                }else{
                  byte checkCarta = temp1.readByte();
                  
                  switch(checkCarta){
                    case 12:{
                      flagCardT1 = 1;
                      int tam = temp1.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp1.read(arrGamb);
                      card1 = new PkmCarta(arrGamb,idT1);
                    }
                    break;
                    case 24:{
                      flagCardT1 = 3;
                      int tam = temp1.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp1.read(arrGamb);
                      card3 = new EnergiaCarta(arrGamb,idT1);
                    }
                    break;
                    case 42:
                      {
                        flagCardT1 = 5;
                      int tam = temp1.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp1.read(arrGamb);
                      card5 = new TreinadorCarta(arrGamb,idT1);               
                      }
                    break;
                    default:
                      System.out.println("Erro ao verificar tipo carta durante intercalação.Byte1: "+ checkCarta);
                    break;
                  }
                }
             }else{
                if(card1 != null){
                  flagCardT1 = 1;
                  idT1 = card1.id;
                }else if(card3 != null){
                  flagCardT1 = 3;
                  idT1 = card3.id;
                }else{
                  flagCardT1 = 5;
                  idT1 = card5.id;
                }
             }
              if(card2 == null && card4 == null && card6 == null){
                idT2 = temp2.readInt();
                if(idT2 == -1){
                  flagCardT2 = -1;
                  temp2End = true;
                }else{
                  byte checkCarta = temp2.readByte();
                 
                  switch(checkCarta){
                    case 12:{
                      flagCardT2 = 2;
                      int tam = temp2.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp2.read(arrGamb);
                      card2 = new PkmCarta(arrGamb,idT2);
                    }
                    break;
                    case 24:{
                      flagCardT2 = 4;
                      int tam = temp2.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp2.read(arrGamb);
                      card4 = new EnergiaCarta(arrGamb,idT2);
                    }
                    break;
                    case 42:
                      {
                        flagCardT2 = 6;
                      int tam = temp2.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp2.read(arrGamb);
                      card6 = new TreinadorCarta(arrGamb,idT2);               
                      }
                    break;
                    default:
                      System.out.println("Erro ao verificar tipo carta durante intercalação.Byte2: "+ checkCarta);
                      
                    break;
                  }
                }
             }else{
                if(card2 != null){
                  flagCardT2 = 2;
                  idT2 = card2.id;
                }else if(card4 != null){
                  flagCardT2 = 4;
                  idT2 = card4.id;
                }else{
                  flagCardT2 = 6;
                  idT2 = card6.id;
                }
             }
             //Agora,descobrir qual carta entre os dois arquivos temporarios é a menor
             switch(flagCardT1){
              case -1:
                switch(flagCardT2){
                  case -1:

                  break;
                  case 2:{
                    if(write34){
                        temp3.writeInt(card2.id);
                        temp3.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card2.id);
                        temp4.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card2 = null;//Anular referencia que foi usada
                  }
                  break;
                  case 4:
                  {
                    if(write34){
                        temp3.writeInt(card4.id);
                        temp3.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card4.id);
                        temp4.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card4 = null;
                  }
                  break;
                  case 6:
                  {
                    if(write34){
                        temp3.writeInt(card6.id);
                        temp3.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card6.id);
                        temp4.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card6 = null;
                  }
                  break;
                  default:
                  System.out.println("Erro flagT2 durante Intercalação.->" + flagCardT2);
                  break;
                }
              break;
              case 1:
                switch(flagCardT2){
                  case -1:
                  {
                  if(write34){
                        temp3.writeInt(card1.id);
                        temp3.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card1.id);
                        temp4.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card1 = null;
                  }
                  break;
                  case 2:
                  {
                    if(sortCarta(card1,card2)){
                      if(write34){
                        temp3.writeInt(card1.id);
                        temp3.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card1.id);
                        temp4.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card1 = null;
                    }else{
                      if(write34){
                        temp3.writeInt(card2.id);
                        temp3.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card2.id);
                        temp4.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card2 = null;
                    }
                  }
                  break;
                  case 4:
                  {
                    if(sortCarta(card1,card4)){
                      if(write34){
                        temp3.writeInt(card1.id);
                        temp3.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card1.id);
                        temp4.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card1 = null;
                    }else{
                      if(write34){
                        temp3.writeInt(card4.id);
                        temp3.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card4.id);
                        temp4.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card4 = null;
                    }
                  }
                  break;
                  case 6:
                  {
                    if(sortCarta(card1,card6)){
                      if(write34){
                        temp3.writeInt(card1.id);
                        temp3.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card1.id);
                        temp4.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card1 = null;
                    }else{
                      if(write34){
                        temp3.writeInt(card6.id);
                        temp3.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card6.id);
                        temp4.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card6 = null;
                    }
                  }
                  break;
                  default:
                  System.out.println("Erro flagT2 durante Intercalação.->" + flagCardT2);
                  break;
                }
              break;
              case 3:
              {
                switch(flagCardT2){
                  case -1:
                  {
                  if(write34){
                        temp3.writeInt(card3.id);
                        temp3.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card3.id);
                        temp4.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card3 = null;
                  }
                  break;
                  case 2:
                  {
                    if(sortCarta(card3,card2)){
                      if(write34){
                        temp3.writeInt(card3.id);
                        temp3.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card3.id);
                        temp4.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card3 = null;
                    }else{
                      if(write34){
                        temp3.writeInt(card2.id);
                        temp3.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card2.id);
                        temp4.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card2 = null;
                    }
                  }
                  break;
                  case 4:
                  {
                    if(sortCarta(card3,card4)){
                      if(write34){
                        temp3.writeInt(card3.id);
                        temp3.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card3.id);
                        temp4.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card3 = null;
                    }else{
                      if(write34){
                        temp3.writeInt(card4.id);
                        temp3.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card4.id);
                        temp4.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card4 = null;
                    }
                  }
                  break;
                  case 6:
                  {
                    if(sortCarta(card3,card6)){
                      if(write34){
                        temp3.writeInt(card3.id);
                        temp3.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card3.id);
                        temp4.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card3 = null;
                    }else{
                      if(write34){
                        temp3.writeInt(card6.id);
                        temp3.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card6.id);
                        temp4.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card6 = null;
                    }
                  }
                  break;
                  default:
                  System.out.println("Erro flagT2 durante Intercalação.->" + flagCardT2);
                  break;
                }
              }
              break;
              case 5:
              {
                switch(flagCardT2){
                  case -1:
                  {
                  if(write34){
                        temp3.writeInt(card5.id);
                        temp3.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card5.id);
                        temp4.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card5 = null;
                  }
                  break;
                  case 2:
                  {
                    if(sortCarta(card5,card2)){
                      if(write34){
                        temp3.writeInt(card5.id);
                        temp3.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card5.id);
                        temp4.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card5 = null;
                    }else{
                      if(write34){
                        temp3.writeInt(card2.id);
                        temp3.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card2.id);
                        temp4.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card2 = null;
                    }
                  }
                  break;
                  case 4:
                  {
                    if(sortCarta(card5,card4)){
                      if(write34){
                        temp3.writeInt(card5.id);
                        temp3.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card5.id);
                        temp4.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card5 = null;
                    }else{
                      if(write34){
                        temp3.writeInt(card4.id);
                        temp3.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card4.id);
                        temp4.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card4 = null;
                    }
                  }
                  break;
                  case 6:
                  {
                    if(sortCarta(card5,card6)){
                      if(write34){
                        temp3.writeInt(card5.id);
                        temp3.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card5.id);
                        temp4.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card5 = null;
                    }else{
                      if(write34){
                        temp3.writeInt(card6.id);
                        temp3.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp3.writeInt(arrG.length);
                        temp3.write(arrG);
                      }else{
                        temp4.writeInt(card6.id);
                        temp4.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp4.writeInt(arrG.length);
                        temp4.write(arrG);
                      }
                      card6 = null;
                    }
                  }
                  break;
                  default:
                  System.out.println("Erro flagT2 durante Intercalação.->" + flagCardT2);
                  break;
                }
              }
              break;
              default:
                System.out.println("Erro flagT1 durante Intercalação.");
              break;
            }
            }
          }

        }
        if(write34){
                temp3.writeInt(-1);
              }else{  
                temp4.writeInt(-1);
              }
        write34 = !write34;
        }
        Or1234 = !Or1234;
      }else{
        temp1.seek(0);
        temp2.seek(0);
        temp3.seek(0);
        temp4.seek(0);
        //Apagar temp 1 e 2
        temp1.setLength(0);
        temp2.setLength(0);
        long size3 = temp3.length();
        long size4 = temp4.length();
        boolean write12 = true;//Se true,estamos escrevendo em 1.Se false,escrevendo em 2
        //Enquanto não terminamos de ler os arquivos temporarios por inteiro
        while(temp3.getFilePointer()!= size3 || temp4.getFilePointer()!= size4){ 
          boolean temp3End = false,temp4End = false;//Para saber se chegamos ao fim dos segmentos
          //Enquanto não terminamos os dois segmentos escolhidos
          PkmCarta card1 = null,card2 = null;//Placeholders
          EnergiaCarta card3 = null,card4 = null;
          TreinadorCarta card5 = null,card6 = null;
          
          if(temp3.getFilePointer()==size3){
            temp3End = true;
          }
          if(temp4.getFilePointer()==size4){
            temp4End = true;
          }
          while(!temp3End || !temp4End){ 
          if(temp3End){
            if(!temp4End){
            int test = temp4.readInt();
            if(test== -1){
              temp4End = true;
            }else{
              if(write12){
                temp1.writeInt(test);
                byte gamb100 = temp4.readByte();
                temp1.writeByte(gamb100);
                int size = temp4.readInt();
                temp1.writeInt(size);
                byte[] arrB = new byte[size];
                temp4.read(arrB);
                temp1.write(writeByteArr(arrB,gamb100));
              }else{
                temp2.writeInt(test);
                byte gamb100 = temp4.readByte();
                temp2.writeByte(gamb100);
                int size = temp4.readInt();
                temp2.writeInt(size);
                byte[] arrB = new byte[size];
                temp4.read(arrB);
                temp2.write(writeByteArr(arrB,gamb100));
              }
            }
            }
          }else{
            if(temp4End){
              int test = temp3.readInt();
            if(test== -1){
              temp3End = true;
            }else{
              if(write12){
                temp1.writeInt(test);
                byte gamb100 = temp3.readByte();
                temp1.writeByte(gamb100);
                int size = temp3.readInt();
                temp1.writeInt(size);
                byte[] arrB = new byte[size];
                temp3.read(arrB);
                temp1.write(writeByteArr(arrB,gamb100));
              }else{
                temp2.writeInt(test);
                byte gamb100 = temp3.readByte();
                temp2.writeByte(gamb100);
                int size = temp3.readInt();
                temp2.writeInt(size);
                byte[] arrB = new byte[size];
                temp3.read(arrB);
                temp2.write(writeByteArr(arrB,gamb100));
              }
            }
            }else{
              int flagCardT1 = 0,flagCardT2 = 0;//Flag para saber qual tipo de carta esta ativa
              int idT1,idT2;
              //Checar se não há carta placeholder que perdeu uma checagem anterior
             if(card1 == null && card3 == null && card5 == null){
                idT1 = temp3.readInt();
                if(idT1 == -1){
                  flagCardT1 = -1;
                  temp3End = true;
                }else{
                  byte checkCarta = temp3.readByte();
                  switch(checkCarta){
                    case 12:{
                      flagCardT1 = 1;
                      int tam = temp3.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp3.read(arrGamb);
                      card1 = new PkmCarta(arrGamb,idT1);
                    }
                    break;
                    case 24:{
                      flagCardT1 = 3;
                      int tam = temp3.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp3.read(arrGamb);
                      card3 = new EnergiaCarta(arrGamb,idT1);
                    }
                    break;
                    case 42:
                      {
                        flagCardT1 = 5;
                      int tam = temp3.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp3.read(arrGamb);
                      card5 = new TreinadorCarta(arrGamb,idT1);               
                      }
                    break;
                    default:
                      System.out.println("Erro ao verificar tipom carta durante intercalação");
                    break;
                  }
                }
             }else{
                if(card1 != null){
                  flagCardT1 = 1;
                  idT1 = card1.id;
                }else if(card3 != null){
                  flagCardT1 = 3;
                  idT1 = card3.id;
                }else{
                  flagCardT1 = 5;
                  idT1 = card5.id;
                }
             }
              if(card2 == null && card4 == null && card6 == null){
                idT2 = temp4.readInt();
                if(idT2 == -1){
                  flagCardT2 = -1;
                  temp4End = true;
                }else{
                  byte checkCarta = temp4.readByte();
                  switch(checkCarta){
                    case 12:{
                      flagCardT2 = 2;
                      int tam = temp4.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp4.read(arrGamb);
                      card2 = new PkmCarta(arrGamb,idT2);
                    }
                    break;
                    case 24:{
                      flagCardT2 = 4;
                      int tam = temp4.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp4.read(arrGamb);
                      card4 = new EnergiaCarta(arrGamb,idT2);
                    }
                    break;
                    case 42:
                      {
                        flagCardT2 = 6;
                      int tam = temp4.readInt();
                      byte[] arrGamb = new byte[tam];
                      temp4.read(arrGamb);
                      card6 = new TreinadorCarta(arrGamb,idT2);               
                      }
                    break;
                    default:
                      System.out.println("Erro ao verificar tipom carta durante intercalação");
                    break;
                  }
                }
             }else{
                if(card2 != null){
                  flagCardT2 = 2;
                  idT2 = card2.id;
                }else if(card4 != null){
                  flagCardT2 = 4;
                  idT2 = card4.id;
                }else{
                  flagCardT2 = 6;
                  idT2 = card6.id;
                }
             }
             //Agora,descobrir qual carta entre os dois arquivos temporarios é a menor
             switch(flagCardT1){
              case -1:
                switch(flagCardT2){
                  case -1:

                  break;
                  case 2:{
                    if(write12){
                        temp1.writeInt(card2.id);
                        temp1.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card2.id);
                        temp2.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card2 = null;//Anular referencia que foi usada
                  }
                  break;
                  case 4:
                  {
                    if(write12){
                        temp1.writeInt(card4.id);
                        temp1.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card4.id);
                        temp2.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card4 = null;
                  }
                  break;
                  case 6:
                  {
                    if(write12){
                        temp1.writeInt(card6.id);
                        temp1.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card6.id);
                        temp2.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card6 = null;
                  }
                  break;
                  default:
                  System.out.println("Erro flagT2 durante Intercalação.->" + flagCardT2);
                  break;
                }
              break;
              case 1:
                switch(flagCardT2){
                  case -1:
                  {
                  if(write12){
                        temp1.writeInt(card1.id);
                        temp1.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card1.id);
                        temp2.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card1 = null;
                  }
                  break;
                  case 2:
                  {
                    if(sortCarta(card1,card2)){
                      if(write12){
                        temp1.writeInt(card1.id);
                        temp1.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card1.id);
                        temp2.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card1 = null;
                    }else{
                      if(write12){
                        temp1.writeInt(card2.id);
                        temp1.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card2.id);
                        temp2.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card2 = null;
                    }
                  }
                  break;
                  case 4:
                  {
                    if(sortCarta(card1,card4)){
                      if(write12){
                        temp1.writeInt(card1.id);
                        temp1.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card1.id);
                        temp2.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card1 = null;
                    }else{
                      if(write12){
                        temp1.writeInt(card4.id);
                        temp1.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card4.id);
                        temp2.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card4 = null;
                    }
                  }
                  break;
                  case 6:
                  {
                    if(sortCarta(card1,card6)){
                      if(write12){
                        temp1.writeInt(card1.id);
                        temp1.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card1.id);
                        temp2.writeByte(12);
                        byte[] arrG = card1.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card1 = null;
                    }else{
                      if(write12){
                        temp1.writeInt(card6.id);
                        temp1.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card6.id);
                        temp2.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card6 = null;
                    }
                  }
                  break;
                  default:
                  System.out.println("Erro flagT2 durante Intercalação.->" + flagCardT2);
                  break;
                }
              break;
              case 3:
              {
                switch(flagCardT2){
                  case -1:
                  {
                  if(write12){
                        temp1.writeInt(card3.id);
                        temp1.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card3.id);
                        temp2.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card3 = null;
                  }
                  break;
                  case 2:
                  {
                    if(sortCarta(card3,card2)){
                      if(write12){
                        temp1.writeInt(card3.id);
                        temp1.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card3.id);
                        temp2.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card3 = null;
                    }else{
                      if(write12){
                        temp1.writeInt(card2.id);
                        temp1.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card2.id);
                        temp2.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card2 = null;
                    }
                  }
                  break;
                  case 4:
                  {
                    if(sortCarta(card3,card4)){
                      if(write12){
                        temp1.writeInt(card3.id);
                        temp1.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card3.id);
                        temp2.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card3 = null;
                    }else{
                      if(write12){
                        temp1.writeInt(card4.id);
                        temp1.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card4.id);
                        temp2.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card4 = null;
                    }
                  }
                  break;
                  case 6:
                  {
                    if(sortCarta(card3,card6)){
                      if(write12){
                        temp1.writeInt(card3.id);
                        temp1.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card3.id);
                        temp2.writeByte(24);
                        byte[] arrG = card3.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card3 = null;
                    }else{
                      if(write12){
                        temp1.writeInt(card6.id);
                        temp1.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card6.id);
                        temp2.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card6 = null;
                    }
                  }
                  break;
                  default:
                  System.out.println("Erro flagT2 durante Intercalação.->" + flagCardT2);
                  break;
                }
              }
              break;
              case 5:
              {
                switch(flagCardT2){
                  case -1:
                  {
                  if(write12){
                        temp1.writeInt(card5.id);
                        temp1.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card5.id);
                        temp2.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card5 = null;
                  }
                  break;
                  case 2:
                  {
                    if(sortCarta(card5,card2)){
                      if(write12){
                        temp1.writeInt(card5.id);
                        temp1.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card5.id);
                        temp2.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card5 = null;
                    }else{
                      if(write12){
                        temp1.writeInt(card2.id);
                        temp1.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card2.id);
                        temp2.writeByte(12);
                        byte[] arrG = card2.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card2 = null;
                    }
                  }
                  break;
                  case 4:
                  {
                    if(sortCarta(card5,card4)){
                      if(write12){
                        temp1.writeInt(card5.id);
                        temp1.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card5.id);
                        temp2.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card5 = null;
                    }else{
                      if(write12){
                        temp1.writeInt(card4.id);
                        temp1.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card4.id);
                        temp2.writeByte(24);
                        byte[] arrG = card4.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card4 = null;
                    }
                  }
                  break;
                  case 6:
                  {
                    if(sortCarta(card5,card6)){
                      if(write12){
                        temp1.writeInt(card5.id);
                        temp1.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card5.id);
                        temp2.writeByte(42);
                        byte[] arrG = card5.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card5 = null;
                    }else{
                      if(write12){
                        temp1.writeInt(card6.id);
                        temp1.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp1.writeInt(arrG.length);
                        temp1.write(arrG);
                      }else{
                        temp2.writeInt(card6.id);
                        temp2.writeByte(42);
                        byte[] arrG = card6.paraByteArray();
                        temp2.writeInt(arrG.length);
                        temp2.write(arrG);
                      }
                      card6 = null;
                    }
                  }
                  break;
                  default:
                  System.out.println("Erro flagT2 durante Intercalação.->" + flagCardT2);
                  break;
                }
              }
              break;
              default:
                System.out.println("Erro flagT1 durante Intercalação.");
              break;
            }
            }
          }
        }
        if(write12){
              temp1.seek(temp1.getFilePointer() - 4);
                if(!(temp1.readInt()==-1)){
                temp1.writeInt(-1);
              }
              }else{
                temp2.seek(temp2.getFilePointer() - 4);
                if(!(temp2.readInt()==-1)){
                temp2.writeInt(-1);
              }
              }
        write12 = !write12;
        }
        Or1234 = !Or1234;
      }
      
    }while(temp1.length()!=0 &&temp2.length()!=0 && temp3.length()!=0 &&temp4.length()!=0);
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  byte[] writeByteArr(byte[] saving,byte me){
    try{
    switch(me){
      case 12:
        return new PkmCarta(saving,-1).paraByteArray();
    
      case 24:
        return new EnergiaCarta(saving,-1).paraByteArray();
    
      case 42:
        return new TreinadorCarta(saving,-1).paraByteArray();
      
    }
    }catch(Exception e){
      e.printStackTrace();
    }
    return new byte[]{1,2,3};
  }
}
