
import java.awt.Toolkit;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Resultado.java
 *
 * Created on 21/06/2011, 10:56:37
 */
/**
 *
 * @author vinicius.rodrigues
 */
public class Resultado extends javax.swing.JFrame {

    private String servico;
    private int totalclientes;//total de clientes gerados
    private int atendidos;//total de clientes que foram atendidos    
    private int totalfila;//total de clientes que entraram na fila fila
    private float time;//tempo final do relógio lógico da simulação
    private float tempo;//tempo de execução da simulação...relogio real
    private float tempofila;//tempo total de clientes na fila    
    private float tempoatendimento;//tempo total de clientes em atendimento
    private float tempofinalchegada;//tempo da última chegada
    
    private int abandonos;//total de clientes que não foram atendidos    
    private float tempomediofila;//tempo médio de espera na fila    
    private float tempomediochegada;//tempo médio entre chegadas
    private float tempomedioatendimento;//tempo médio de atendimento
    private float temposervocioso;//tempo total de osiosidade dos servos
    

    /** Creates new form Relatório */
    public Resultado(String srv) {
        initComponents();
        this.setTitle(srv);
        this.setLocationRelativeTo(null);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("sina.png")));
        this.servico = srv;
        this.totalclientes = 0;
        this.atendidos = 0;
        this.abandonos = 0;
        this.totalfila = 0;
        this.time = 0;
        this.tempofila = 0;
        this.tempomediofila = 0;
        this.tempoatendimento = 0;
        this.tempofinalchegada = 0;
        this.tempomedioatendimento = 0;
        this.temposervocioso = 0;
    }
    
    public void gera(int servos) {//número de servos
        
        int aux;//auxiliar para fomatar casas decimais
        
        this.abandonos = this.totalclientes - this.atendidos;        
        this.tempomediofila = this.tempofila/this.totalfila;
        this.tempomediochegada = this.tempofinalchegada/this.totalclientes;
        this.tempomedioatendimento= this.tempoatendimento/this.atendidos;
        this.temposervocioso = (this.time * servos) - this.tempoatendimento;
        
        this.jTextArea1.append("Estatísticas do serviço " + this.servico + "\n\n");
        
        this.jTextArea1.append("Total de clientes gerados:........................................................"
                + this.get_totalclientes() + "\n\n");
        
        aux = (int) (((float)this.get_atendidos()/(float)this.get_totalclientes()) * 10000);
        this.jTextArea1.append("Total de clientes atendidos:....................................................." 
                + this.get_atendidos() + " - " + (float) aux/100 + "%\n\n");
        
        aux = (int) (((float)this.get_abandonos()/(float)this.get_totalclientes()) * 10000);
        this.jTextArea1.append("Total de clientes que abandonaram:......................................" 
                + this.get_abandonos() + " - " + (float) aux/100 + "%\n\n");
        
        aux = (int) (((float)this.get_totalfila()/(float)this.get_atendidos()) * 10000);
        this.jTextArea1.append("Total de clientes que entraram na fila:..................................." 
                + this.get_totalfila() + " - " + (float) aux/100 + "%\n\n");
        
        this.jTextArea1.append("Tempo total de clientes na fila:................................................" 
                + (int) this.get_tempofila() + "\n\n");
        
        this.jTextArea1.append("Tempo médio de espera na fila:.............................................." 
                + (int) this.get_tempomediofila() + "\n\n");
        
        this.jTextArea1.append("Tempo total de clientes em atendimento:.............................." 
                + (int) this.get_tempoatendimento() + "\n\n");
        
        this.jTextArea1.append("Tempo medio de chegadas:....................................................." 
                + (int) this.get_tempomediochegada() + "\n\n");
        
        this.jTextArea1.append("Tempo medio de atendimento:................................................." 
                + (int) this.get_tempomedioatendimento() + "\n\n");
        
        aux = (int) (((float) this.get_temposervocioso()/((float) this.get_time() * servos)) * 10000);
        this.jTextArea1.append("Tempo total de servos ociosos:..............................................." 
                + (int) this.get_temposervocioso() + " - " + (float) aux/100 + "%\n\n");
        
        this.jTextArea1.append("Tempo final:.................................................................................." 
                + (int) this.get_time() + "\n\n");
        
        this.jTextArea1.append("Simulação realizada em " 
                + (int) this.get_tempo() + " segundos.");
    }
    
    public void add_cliente(){
        this.totalclientes++;
    }
    
    public void add_clientefila(){
        this.totalfila++;
    }        
    
    public void add_tempofila(float f){
        this.tempofila = this.tempofila + f;
    }
            
    public void add_tempoatendimento(float f){
        this.tempoatendimento = this.tempoatendimento + f;
    }
    
    public void add_atendidos(){
        this.atendidos++;
    }
    
    public void set_tempofinalchegada(float f){
        this.tempofinalchegada = f;
    }
    
    public void set_time(float f, float t){
        this.tempo = t / 1000;
        this.time = f;
    }            
    
    public int get_totalclientes(){
        return this.totalclientes;
    }
    
    public float get_time(){
        return this.time;
    }
    
    public float get_tempo(){
        return this.tempo;
    }
    
    public int get_atendidos(){
        return this.atendidos;
    }
    
    public int get_abandonos(){
        return this.abandonos;
    }
    
    public int get_totalfila(){
        return this.totalfila;
    }
    
    public float get_tempofila(){
        return this.tempofila;
    }
    
    public float get_tempomediofila(){
        return this.tempomediofila;
    }
    
    public float get_tempomediochegada(){
        return this.tempomediochegada;
    }
    
    public float get_tempoatendimento(){
        return this.tempoatendimento;
    }
    
    public float get_tempomedioatendimento(){
        return this.tempomedioatendimento;
    }
    
    public float get_temposervocioso(){
        return this.temposervocioso;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setResizable(false);

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

}
