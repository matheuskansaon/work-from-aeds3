/*Classe Main enxuta,apenas inicia uma instância da classe que
* realmente faz as operações,começa elas,e apresenta uma mensagem
* ao usuário quando ele escolhe sair.
*/
class PTCGCRUD{
  public static void main(String[] args) {
      PTCGUI program = new PTCGUI();
      program.init();
      System.out.println("----------Até a próxima!----------");
  }
}
