/*
 * Esta é a classe que representa cada evento ocorrido na simulação
 * cada objeto desta classe é um evento que pode ser controlado de forma independente
 */

/**
 *
 * @author vinicius.rodrigues
 */
public class Cliente {
       
    private int servico; //serviço 1 é borracharia e serviço 2 é elétrica
    private int prioridade; //prioridade 1 é a mais alta e prioridade 2 é a mais baixa
    private float tempo_chegada; //hora em que chegou
    private float tempo_fila; //tempo que ficou na fila
    private float tempo_atendimento; //tempo de atendiento
    private float tempo_ini_atendimento; //tempo de inicio do atendimento
    private float tempo_fim_atendimento; //tempo de saída do sistema
    private int seq;    
    
    public Cliente(int sequ, int serv, int priori, float chegada, float tempoatendimento){
        this.servico = serv;
        this.prioridade = priori;
        this.tempo_chegada = chegada;
        this.tempo_atendimento = tempoatendimento;
        this.seq = sequ;
    }
    
    public Cliente(int sequ, int serv, float chegada, float tempoatendimento){
        this.servico = serv;
        this.prioridade = 1;
        this.tempo_chegada = chegada;
        this.tempo_atendimento = tempoatendimento;
        this.seq = sequ;
    }
    
    //seters
    public void set_servico(int serv){
        this.servico = serv;
    }
    
    public void set_prioridade(int pri){
        this.prioridade = pri;
    }
        
    public void set_tempo_chegada(float time){
        this.tempo_chegada = time;
    }
    
    public void set_tempo_fila(float time){
        this.tempo_fila = time - this.tempo_chegada;
    }
    
    public void set_tempo_atendimento(float time){
        this.tempo_atendimento = time;
    }
    
    public void set_tempo_ini_atendimento(float time){
        this.tempo_ini_atendimento = time;
    }
    
    public void set_tempo_fim_atendimento(float time){
        this.tempo_fim_atendimento = time  + this.tempo_atendimento;
    }
    
    public void set_seq(int s){
        this.seq = s;
    }
    
    //geters
    public int get_servico(){
        return this.servico;
    }
    
    public int get_prioridade(){
        return this.prioridade;
    }
    
    public float get_tempo_chegada(){
        return this.tempo_chegada;
    }    
        
    public float get_tempo_fila(){
        return this.tempo_fila;
    }
    
    public float get_tempo_atendimento(){
        return this.tempo_atendimento;
    }
        
    public float get_tempo_ini_atendimento(){
        return this.tempo_ini_atendimento;
    }
    
    public float get_tempo_fim_atendimento(){
        return this.tempo_fim_atendimento;
    }
    
    public int get_seq(){
        return this.seq;
    }

}
