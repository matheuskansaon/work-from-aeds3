/* Classe pai das que armazenam os dados de uma carta do jogo,com algumas alterações
* feitas em relação ao jeito como as informações estão armazenadas na base de dados
* selecionada.
*/
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Locale;

class Carta{
    //---------------
    //Atributos
    //---------------

    int id;//id da carta,baseada na ordem de inserção no programa
    String nome;//nome da carta,como seria referenciada em um jogo
    String colecao;/*nome da coleção na qual a carta foi lançada.
    *Existem cartas que não são lançadas em coleções normais,elas geralmente serão marcadas aqui 
    *com a "era" em que foram lançadas mais seu status como promo.Ex: BW-Promo,Sun & Moon Promo
    *Existem promos que são identificadas de maneira diferente,no entanto.
    */
    int numeracao;//Numeração da carta na coleção em que foi lançada
    Date dataLancamento;/*data de lançamento da carta(dia,mes e ano),a data de lançamento da coleção a qual
    * a carta pertence (para cartas não promo).Para cartas promo,como cada uma teria uma
    * data de lançamento diferente,todas as cartas promo da mesma era tem como data de lançamento
    * a mesma data da primeira coleção da era em questão.
    */
    String illustrator;//Indica o ilustrador da arte que aparece na carta.
    
    //--------------
    //Construtores
    //--------------
    Carta(){

    }
    Carta(byte[] arr,int identificacao){

    }
    // Neste construtor,passamos todos os dados obtidos da base de dados no argumento info,mais o id da carta como numb
    Carta(String[] info,int numb){
        id = numb;
        nome = info[1];
        String numberExpan = "";
        //Vamos extrair a informação numeração do primeiro numero que aparece no campo "name" na base de dados
        int count = 0;
        int size = info[2].length();
        while(count<size && !(info[2].charAt(count)>='0' && info[2].charAt(count)<='9')){
            count++;
        }
        while(count<size && info[2].charAt(count)>='0' && info[2].charAt(count)<='9'){
            numberExpan+= info[2].charAt(count);
            count++;
        }
        if(!numberExpan.isEmpty()){
        numeracao = Integer.parseInt(numberExpan);
        }else{
            numeracao = 0;//Existem numerações,em algumas poucas coleções,que não usam nenhum número.
        }
        colecao = info[4];
        dataLancamento = achaDataExpan(info[4]);
        
        illustrator = info[6];
        
    }
    //Construtor
    //---------------
    //Metodos
    //---------------
    /* achaDataExpan -> acha a data de lancamento da carta,com base na colecao em que ela foi lançada.
    * @param (String colecao) colecao em que a carta foi lançada
    * @return (Date) data(dia,mes,ano) em que a carta foi lançada
    */
    Date achaDataExpan(String colecao){
        Date resp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM,yyyy",Locale.US);
        try{
            //Coleções promocionais
        if(colecao.contains("Promo") || colecao.contains("A Alternate")){
            if(colecao.contains("HS")){
                resp = sdf.parse("10 Feb,2010");
            }else if(colecao.contains("BW")){
                resp = sdf.parse("25 Apr,2011");
            }else if(colecao.contains("XY")){
                resp = sdf.parse("5 Feb,2014");
            }else if(colecao.contains("Sun & Moon") || colecao.contains("A Alternate")){
                resp = sdf.parse("3 Apr,2017");
            }else if(colecao.contains("Sword & Shield")){
                resp = sdf.parse("7 Feb,2020");
            }else{
                throw new Exception("Data:Promo não indicada.");//Usado para pegar promo que não se encaixam nos padrões
                //acima,caso tenhamos esquecido alguma ou a base de dados cresça.
            }
        }else if(colecao.contains("EX ")){
            //Coleções Geração 3
            if(colecao.contains("Ruby & Sapphire")){
                resp = sdf.parse("18 Jun,2003");
            }else if(colecao.contains("Sandstorm")){
                resp = sdf.parse("18 Sep,2003");
            }else if(colecao.contains("Dragon") && !colecao.contains("Frontiers")){
                resp = sdf.parse("24 Nov,2003");
            }else if(colecao.contains("Team Magma vs. Team Aqua")){
                resp = sdf.parse("15 Mar,2004");
            }else if(colecao.contains("Hidden Legends")){
                resp = sdf.parse("14 Jun,2004");
            }else if(colecao.contains("Fire Red & Leaf Green")){
                resp = sdf.parse("30 Aug,2004");
            }else if(colecao.contains("Team Rocket Returns")){
                resp = sdf.parse("8 Nov,2004");
            }else if(colecao.contains("Deoxys")){
                resp = sdf.parse("14 Feb,2005");
            }else if(colecao.contains("Emerald")){
                resp = sdf.parse("9 May,2005");
            }else if(colecao.contains("Unseen Forces")){
                resp = sdf.parse("22 Aug,2005");
            }else if(colecao.contains("Delta Species")){
                resp = sdf.parse("31 Oct,2005");
            }else if(colecao.contains("Legend Maker")){
                resp = sdf.parse("13 Feb,2006");
            }else if(colecao.contains("Holon Phantoms")){
                resp = sdf.parse("3 May,2006");
            }else if(colecao.contains("Crystal Guardians")){
                resp = sdf.parse("30 Aug,2006");
            }else if(colecao.contains("Dragon Frontiers")){
                resp = sdf.parse("8 Nov,2006");
            }else if(colecao.contains("Power Keepers")){
                resp = sdf.parse("14 Feb,2007");
            }else{
                throw new Exception("Data:Coleção Geração 3 não indicada.");
            }
        }else if(colecao.contains("Diamond & Pearl")){
            //Coleções Geração 4 Era DP
            if(!colecao.contains("—")){
                resp = sdf.parse("23 May,2007");
            }else if(colecao.contains("Mysterious Treasures")){
                resp = sdf.parse("22 Aug,2007");
            }else if(colecao.contains("Secret Wonders")){
                resp = sdf.parse("7 Nov,2007");
            }else if(colecao.contains("Great Encounters")){
                resp = sdf.parse("13 Feb,2008");
            }else if(colecao.contains("Majestic Dawn")){
                resp = sdf.parse("21 May,2008");
            }else if(colecao.contains("Legends Awakened")){
                resp = sdf.parse("20 Aug,2008");
            }else if(colecao.contains("Stormfront")){
                resp = sdf.parse("5 Nov,2008");
            }else{
                throw new Exception("Data:Coleção Geração 4,Era DP,não indicada.");
            }
        }else if(colecao.contains("Platinum")){
            //Coleções Geração 4 Era P
            if(!colecao.contains("—") && !colecao.contains("Arceus")){
                resp = sdf.parse("11 Feb,2009");
            }else if(colecao.contains("Rising Rivals")){
                resp = sdf.parse("16 May,2009");
            }else if(colecao.contains("Supreme Victors")){
                resp = sdf.parse("19 Aug,2009");
            }else if(colecao.contains("Arceus")){
                resp = sdf.parse("4 Nov,2009");
            }else{
                throw new Exception("Data:Coleção Geração 4,Era P,não indicada.");
            }
        }else if(colecao.contains("HeartGold & SoulSilver")){ 
            //Coleções Geração 4 Era HGSS
            resp = sdf.parse("10 Feb,2010");
        }else if(colecao.contains("HS—")){
            if(colecao.contains("Unleashed")){
                resp = sdf.parse("12 May,2010");
            }else if(colecao.contains("Undaunted")){
                resp = sdf.parse("18 Aug,2010");
            }else if(colecao.contains("Triumphant")){
                resp = sdf.parse("3 Nov,2010");
            }else{
                throw new Exception("Data:Coleção Geração 4,Era HS,não indicada.");
            }
        }else if(colecao.contains("Call of Legends")){
            resp = sdf.parse("9 Feb,2011");
        }else if(colecao.contains("Black & White")){
            //Coleções Geração 5
            if(!colecao.contains("—")){
                resp = sdf.parse("25 Apr,2011");
            }else if(colecao.contains("Emerging Powers")){
                resp = sdf.parse("31 Aug,2011");
            }else if(colecao.contains("Noble Victories")){
                resp = sdf.parse("16 Nov,2011");
            }else if(colecao.contains("Next Destinies")){
                resp = sdf.parse("8 Feb,2012");
            }else if(colecao.contains("Dark Explorers")){
                resp = sdf.parse("9 May,2012");
            }else if(colecao.contains("Dragons Exalted")){
                resp = sdf.parse("15 Aug,2012");
            }else if(colecao.contains("Boundaries Crossed")){
                resp = sdf.parse("7 Nov,2012");
            }else if(colecao.contains("Plasma Storm")){
                resp = sdf.parse("6 Feb,2013");
            }else if(colecao.contains("Plasma Freeze")){
                resp = sdf.parse("8 May,2013");
            }else if(colecao.contains("Plasma Blast")){
                resp = sdf.parse("14 Aug,2013");
            }else if(colecao.contains("Legendary Treasures")){
                resp = sdf.parse("6 Nov,2013");
            }else{
                throw new Exception("Data:Coleção Geração 5 não indicada.");
            }
        }else if(colecao.contains("XY")){
            //Coleções Geração 6
            if(!colecao.contains("—") && !colecao.contains("BREAKthrough")){
                resp = sdf.parse("5 Feb,2014");
            }else if(colecao.contains("Kalos Starter Set")){
                resp = sdf.parse("8 Nov,2013");
            }else if(colecao.contains("Flashfire")){
                resp = sdf.parse("7 May,2014");
            }else if(colecao.contains("Furious Fists")){
                resp = sdf.parse("13 Aug,2014");
            }else if(colecao.contains("Phantom Forces")){
                resp = sdf.parse("5 Nov,2014");
            }else if(colecao.contains("Primal Clash")){
                resp = sdf.parse("4 Feb,2015");
            }else if(colecao.contains("Roaring Skies")){
                resp = sdf.parse("6 May,2015");
            }else if(colecao.contains("Ancient Origins")){
                resp = sdf.parse("12 Aug,2015");
            }else if(colecao.contains("BREAKthrough")){
                resp = sdf.parse("4 Nov,2015");
            }else if(colecao.contains("BREAKpoint")){
                resp = sdf.parse("3 Feb,2016");
            }else if(colecao.contains("Fates Collide")){
                resp = sdf.parse("2 May,2016");
            }else if(colecao.contains("Steam Siege")){
                resp = sdf.parse("3 Aug,2016");
            }else if(colecao.contains("Evolutions")){
                resp = sdf.parse("2 Nov,2016");
            }else{
                throw new Exception("Data:Coleção Geração 6 não indicada.");
            }
        }else if(colecao.contains("Sun & Moon")){
            //Coleções Geração 7
            resp = sdf.parse("3 Feb,2017");
        }else if(colecao.contains("Guardians Rising")){
            resp = sdf.parse("5 May,2017");
        }else if(colecao.contains("Burning Shadows")){
            resp = sdf.parse("4 Aug,2017");
        }else if(colecao.contains("Crimson Invasion")){
            resp = sdf.parse("3 Nov,2017");
        }else if(colecao.contains("Ultra Prism")){
            resp = sdf.parse("2 Feb,2018");
        }else if(colecao.contains("Forbidden Light")){
            resp = sdf.parse("4 May,2018");
        }else if(colecao.contains("Celestial Storm")){
            resp = sdf.parse("3 Aug,2018");
        }else if(colecao.contains("Lost Thunder")){
            resp = sdf.parse("2 Nov,2018");
        }else if(colecao.contains("Team Up")){
            resp = sdf.parse("1 Feb,2019");
        }else if(colecao.contains("Unbroken Bonds")){
            resp = sdf.parse("3 May,2019");
        }else if(colecao.contains("Unified Minds")){
            resp = sdf.parse("2 Aug,2019");
        }else if(colecao.contains("Cosmic Eclipse")){
            resp = sdf.parse("1 Nov,2019");
        }else if(colecao.contains("Sword & Shield")){
            //Coleções Geração 8
            resp = sdf.parse("7 Feb,2020");
        }else if(colecao.contains("Rebel Clash")){
            resp = sdf.parse("1 May,2020");
        }else if(colecao.contains("Darkness Ablaze")){
            resp = sdf.parse("14 Aug,2020");
        }else if(colecao.contains("Vivid Voltage")){
            resp = sdf.parse("13 Nov,2020");
        }else if(colecao.contains("Battle Styles")){
            resp = sdf.parse("19 Mar,2021");
        }else if(colecao.contains("Chilling Reign")){
            resp = sdf.parse("18 Jun,2021");
        }else if(colecao.contains("Evolving Skies")){
            resp = sdf.parse("27 Aug,2021");
        }else if(colecao.contains("Fusion Strike")){
            resp = sdf.parse("12 Nov,2021");
        }else if(colecao.contains("Brilliant Stars")){
            resp = sdf.parse("25 Feb,2022");
        }else if(colecao.contains("Astral Radiance")){
            resp = sdf.parse("27 May,2022");
        }else if(colecao.contains("Lost Origin")){
            resp = sdf.parse("9 Sep,2022");
        }else if(colecao.contains("Silver Tempest")){
            resp = sdf.parse("11 Nov,2022");
        }else if(colecao.contains("Dragon Vault")){
            //Coleções especiais
            resp = sdf.parse("5 Oct,2012");
        }else if(colecao.contains("Double Crisis")){
            resp = sdf.parse("25 Mar,2015");
        }else if(colecao.contains("Generations")){
            resp = sdf.parse("22 Feb,2016");
        }else if(colecao.contains("Shining Legends")){
            resp = sdf.parse("6 Oct,2017");
        }else if(colecao.contains("Dragon Majesty")){
            resp = sdf.parse("7 Sep,2018");
        }else if(colecao.contains("Detective Pikachu")){
            resp = sdf.parse("29 Mar,2019");
        }else if(colecao.contains("Hidden Fates")){
            resp = sdf.parse("23 Aug,2019");
        }else if(colecao.contains("Champion")&&colecao.contains("Path")){
            //Uso de um caractere Unicode na base de dados no nome desta coleção,acredito que pode
            //dar problema em tempo de execução,por isso a checagem diferente aqui
            resp = sdf.parse("25 Sep,2020");
        }else if(colecao.contains("Shining Fates")){
            resp = sdf.parse("19 Feb,2021");
        }else if(colecao.contains("Celebrations")){
            resp = sdf.parse("8 Oct,2021");
        }else if(colecao.contains("Pokémon GO")){
            resp = sdf.parse("1 Jul,2022");
        }else if(colecao.contains("Crown Zenith")){
            resp = sdf.parse("20 Jan,2023");
        }else if(colecao.contains("POP Series")){
            resp = sdf.parse("1 Sep,2004");
        }else if(colecao.compareTo("Energy")==0){
            //Existem cartas na base de dados sem a coleção certa indicada,estes terão a data do tempo de execução
            //do programa como data
            resp = new Date();
        }else{
            throw new Exception("Data:Coleção não encontrada.");
        }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resp;
    }
}