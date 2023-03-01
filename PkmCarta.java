/*Classe filha de Carta.java,que armazena dados de uma carta que necessariamente
*é de algum pokémon.*/
import java.text.SimpleDateFormat;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.util.Scanner;
import java.util.Locale;

class PkmCarta extends Carta{
    //---------------
    //Atributos
    //---------------

    String evolveDe;//Se o pokemon desta carta evolve de alguém,esse alguém será indicado aqui.
    //Se não,este campo será igual a "null"
    String estagio;//Indica se é um pokémon basico,estagio 1 ou 2,ou pokémon EX,GX,V,dentre outros.
    int vida;//Indica a vida(Hit Points/HP) desta carta.é possível este campo ser null,pois certas cartas
    //de pokémon tem algumas informações espalhadas em diferentes cartas.Ex:V-UNION,LEGEND.
    String pokePwrBdy;//Armazena o nome,e a descrição de um poke-power ou poke-body
    //que o pokemon desta carta possui.Esmagadora maioria das cartas possui apenas um dentre os mencionados
    //ou nenhum deles,caso em que este campo será vazio.Separador será o simbolo '@'
    String tipo;//Armazena uma string que representa o tipo ou tipos do pokemon desta carta.
    //Contudo,parece haver um problema na base de dados selecionada porque alguns poucos pokemons ou estão sem tipo,
    //Ou estão com o tipo errado.
    String resistencia;//Se o pokemon desta carta resistir algum tipo,ele será indicado aqui.
    int custoRecuo;//Cada uso da palavra Colorless neste campo indica +1 no custo de recuo do pokemon.É possivel ser 0 esse custo.
    String ataqueHabilidade;//Aramazena o(s) ataque(s) e/ou habilidade(s) desta carta pokemon.
    String fraqueza;//Se o pokemon desta carta for fraco a um tipo,esse tipo será indicado aqui
    
    //--------------
    //Construtores
    //--------------
    
    //Construtor usado na leitura direta da base de dados
    PkmCarta(String[] info,int numb){
        super(info,numb);
        estagio = info[0];
        evolveDe = info[3];
        if(info[5].compareTo("null")!=0){
            //Pela lógica do jogo,não existem pokemon jogáveis sem vida,mas existem cartas de pokémon
            //jogáveis sem vida(Pokemon é formado por mais de uma carta)
            vida = Integer.parseInt(info[5]);
        }else{
            vida = 0;
        }
        String habilite = "";//Montar a string com pokebodies/pokepowers do pokemon(se ele tiver)
        //Checar se a string de info ou é nula ou não contem nada
        if(!info[7].isEmpty() && info[7].compareTo("null")!=0){
            int count = 0;
            int size = info[7].length();
            boolean readN = false;//Verifica se alcançamos um certo ponto da string vinda de info
            while(count<size){
                if(readN){
                    if(info[7].charAt(count)==':'){
                        readN = false;//Para cartas com mais de um pokebody serem lidas corretamente
                        while(info[7].charAt(count)!='}'){
                            if(info[7].charAt(count)!='"'){
                                habilite +=info[7].charAt(count);
                            }
                            count++;
                        }
                    }
                }else{
                    if(info[7].charAt(count)==':'){
                        readN =true;
                        if(!habilite.isEmpty()){
                            habilite += '@';//Para cartas com mais de um pokebody
                        }
                        while(info[7].charAt(count)!=','){
                            if(info[7].charAt(count)!='"'){
                                habilite +=info[7].charAt(count);
                            }
                            count++;
                        }
                        habilite+='@';
                    }
                    
                }
                count++;
            }
        }
        if(!info[8].isEmpty() && info[8].compareTo("null")!=0){
            int count = 0;
            int size = info[8].length();
            boolean readN = false;
            while(count<size){
                if(readN){
                    if(info[8].charAt(count)==':'){
                        readN = false;//Para cartas com mais de um pokepower serem lidas corretamente
                        while(info[8].charAt(count)!='}'){
                            if(info[8].charAt(count)!='"'){
                                habilite +=info[8].charAt(count);
                            }
                            count++;
                        }
                    }
                }else{
                    if(info[8].charAt(count)==':'){
                        readN =true;
                        if(!habilite.isEmpty()){
                            habilite += '@';//Para cartas com mais de um pokepower
                        }
                        while(info[8].charAt(count)!=','){
                            if(info[8].charAt(count)!='"'){
                                habilite +=info[8].charAt(count);
                            }
                            count++;
                        }
                    }
                    habilite+='@';
                }
                count++;
            }
        }
        pokePwrBdy = habilite;
        tipo = info[9];
        if(info[10].isEmpty() || info[10].compareTo("null")==0){
            resistencia = "Nenhuma";
        }else{
            resistencia = info[10];
        }
        if(info[11].isEmpty() || info[11].compareTo("null")==0){
            custoRecuo = 0;
        }else{
            int count=0, wordCount=0;
            int size = info[11].length();
            while(count<size){
                if(info[11].charAt(count)=='C'){
                    wordCount++;
                }
                count++;
            }
            custoRecuo = wordCount;
        }
        String atck = "";//Montar uma string com ataques/habilidades do pokemon
       if(!info[12].isEmpty() && info[12].compareTo("null")!=0){
        int count = 0,virgulaCount = 0;
        int size = info[12].length();
        boolean readN = false;
        while(count<size){
            if(readN){
                if(info[12].charAt(count)==','){
                    virgulaCount++;
                    if(virgulaCount == 4){
                        while(info[12].charAt(count)!=':'){
                            count++;
                        }
                        count+=3;
                        if(info[12].charAt(count)=='l'){
                            count-=2;//Para ataques sem efeito extra
                        }
                        while(info[12].charAt(count)!='"' && info[12].charAt(count)!='}'){
                            atck+=info[12].charAt(count);
                            count++;
                        }
                        count--;
                        readN = false;
                    }
                }
            }else{
                if(info[12].charAt(count)==','){
                    if(virgulaCount == 1){
                        if(info[12].charAt(count-1)==']'){
                            //Caso especial onde a virgula pode valer ou não,para nossa lógica
                            virgulaCount++;
                        }
                    }else{
                        virgulaCount++;
                    }
                    if(virgulaCount == 2){
                        while(info[12].charAt(count)!=':'){
                            count++;
                        }
                        count+=3;
                        if(!atck.isEmpty()){
                            atck += '@';//Para cartas com mais de um ataque/habilidade
                        }
                        String gamb = "";
                        while(info[12].charAt(count)!='"'){
                            gamb+=info[12].charAt(count);
                            count++;
                        }
                        if(gamb.compareTo("ll,")==0){
                                //Existem alguns erros na base de dados,habilidades/ataques sem nome,quando deveriam ter,
                            atck+="null";
                            count-=2;
                            
                        }else{
                            atck+=gamb;
                        }
                        atck+='@';
                        readN = true;
                    }else if(virgulaCount == 5){
                        virgulaCount = 0;
                    }
                }
            }
            count++;
        }
       }
       ataqueHabilidade = atck;
        fraqueza = info[13];
    }

    //Construtor usado quando ocorre a leitura do arquivo sequencial
    PkmCarta(byte[] arr,int identificacao){
        super(arr,identificacao);
        try{
        ByteArrayInputStream bais = new ByteArrayInputStream(arr);
        DataInputStream dis = new DataInputStream(bais);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy",Locale.US);
        estagio = dis.readUTF();
        nome = dis.readUTF();
        numeracao = dis.readInt();
        evolveDe = dis.readUTF();
        colecao = dis.readUTF();
        vida = dis.readInt();
        illustrator = dis.readUTF();
        pokePwrBdy = dis.readUTF();
        tipo = dis.readUTF();
        resistencia = dis.readUTF();
        custoRecuo = dis.readInt();
        ataqueHabilidade = dis.readUTF();
        fraqueza = dis.readUTF();
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
        dos.writeUTF(estagio);
        dos.writeUTF(nome);
        dos.writeInt(numeracao);
        dos.writeUTF(evolveDe);
        dos.writeUTF(colecao);
        dos.writeInt(vida);
        dos.writeUTF(illustrator);
        dos.writeUTF(pokePwrBdy);
        dos.writeUTF(tipo);
        dos.writeUTF(resistencia);
        dos.writeInt(custoRecuo);
        dos.writeUTF(ataqueHabilidade);
        dos.writeUTF(fraqueza);
        dos.writeUTF(sdf.format(dataLancamento));
        return baos.toByteArray();
    }

    /* mostrar -> mostra todas as informações desta carta na saída padrão.
    */
    public void mostrar(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy",Locale.US);
        System.out.println("ID: "+ id + " / Estagio: " + estagio);
        System.out.println("Nome: "+ nome +" / Numeração: " + numeracao);
        System.out.println("Evolve de: "+ evolveDe + " / Coleção original: " + colecao);
        System.out.println("Vida: " + vida + " / Ilustrador(a): " + illustrator);
        System.out.println("Poke-Power e/ou Poke-Body: " + pokePwrBdy + " / Tipo(s) deste pokémon:" + tipo);
        System.out.println("Resistência: " + resistencia + " / Custo de recuo: " + custoRecuo);
        System.out.println("Ataque(s)/Habilidade(s): " + ataqueHabilidade + " / Fraqueza:" + fraqueza);
        System.out.println("Data de lançamento: " + sdf.format(dataLancamento));
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
            System.out.println("1 - Estagio da carta:");
            System.out.println("2 - Nome da carta:");
            System.out.println("3 - Numeração da carta:");
            System.out.println("4 - De quem esta carta evolve:");
            System.out.println("5 - Coleção original desta carta:");
            System.out.println("6 - Vida desta carta:");
            System.out.println("7 - Poke-Power ou Poke-Body desta carta:");
            System.out.println("8 - Tipo(s) desta carta:");
            System.out.println("9 - Resistência desta carta:");
            System.out.println("10 - Custo de Recuo desta carta:");
            System.out.println("11 - Ataque(s) e/ou habilidade(s) desta carta:");
            System.out.println("12 - Fraqueza desta carta:");
            System.out.println("13 - Data de lançamento desta carta:");
            System.out.println("14 - Ilustrador(a) desta carta:");
            System.out.println("0 - Para sair:");
            System.out.println("(Digite o número e aperte Enter)");
            System.out.println("----------------------------------");
            escolha = Integer.parseInt(s.nextLine());
            switch(escolha){
                case 1:
                System.out.println("Estagio atual: " + estagio);
                System.out.println("Digite um novo estagio(String)");
                estagio = s.nextLine();
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
                System.out.println("De quem esta carta evolve agora: " + evolveDe);
                System.out.println("Digite uma nova pre-evolução(String)");
                evolveDe = s.nextLine();
                break;
                case 5:
                System.out.println("Coleção original desta carta agora: " + colecao);
                System.out.println("Digite uma nova coleção original(String)");
                colecao = s.nextLine();
                break;
                case 6:
                System.out.println("Vida atual: " + vida);
                System.out.println("Digite uma nova vida(int)");
                vida = Integer.parseInt(s.nextLine());
                break;
                case 7:
                {
                String temp = "";
                System.out.println("Poke-Power ou Poke-Body desta carta agora: " + pokePwrBdy);
                System.out.println("Digite um novo nome de Poke-Power ou Poke-Body(String)");
                temp += s.nextLine();
                temp += "@";
                System.out.println("Digite agora a descrição desse Poke-Power ou Poke-Body(String)");
                temp += s.nextLine();
                pokePwrBdy = temp;
                }
                break;
                case 8:
                System.out.println("Tipo(s) desta carta agora: " + tipo);
                System.out.println("Digite um(ou mais) tipo(s) desta carta(String)");
                tipo = s.nextLine();
                break;
                case 9:
                System.out.println("Tipo que esta carta resiste agora: " + resistencia);
                System.out.println("Digite um tipo para esta carta resistir(String)");
                resistencia = s.nextLine();
                break;
                case 10:
                System.out.println("Custo de Recuo atual: " + custoRecuo);
                System.out.println("Digite um novo custo de recuo(int)");
                custoRecuo = Integer.parseInt(s.nextLine());
                break;
                case 11:
                {
                    System.out.println("Ataque(s)/Habilidade(s) desta carta agora: " + ataqueHabilidade);
                    int escolha2 = 0;
                    String temp2 = "";
                    while(escolha2 == 0){
                    String temp = "";
                    System.out.println("Digite um novo nome de Ataque ou Habilidade(String)");
                    temp += s.nextLine();
                    temp += "@";
                    System.out.println("Digite agora a descrição desse Ataque ou Habilidade(String)");
                    temp += s.nextLine();
                    System.out.println("Se você deseja adicionar outro ataque ou habilidade,digite 0(int)");
                    System.out.println("Se não,digite qualquer outro número(int)");
                    escolha2 = Integer.parseInt(s.nextLine());
                    if(escolha2 == 0){
                        temp += "@";
                    }
                    temp2 += temp;
                    }
                    ataqueHabilidade = temp2;
                }
                break;
                case 12:
                System.out.println("Tipo que esta carta é fraca contra agora: " + fraqueza);
                System.out.println("Digite um tipo para ser a fraqueza desta carta (String)");
                fraqueza = s.nextLine();
                break;
                case 13:
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
                case 14:
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