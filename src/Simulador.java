
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


//import jsc.distributions.Exponential;
import jsc.distributions.Lognormal;
import jsc.distributions.Uniform;

import umontreal.iro.lecuyer.probdist.ExponentialDist;

/*
 * Classe principal
 * Esta é a classe onde o core do simulador irá executar
 */

/**
 *
 * @author vinicius.rodrigues
 */
public class Simulador extends Thread {
    
    private ArrayList<Cliente> clientes;   //array com todos os eventos gerados
    private ArrayList<Cliente> fila;      //array que representa a fila de chegada
    private ArrayList<Cliente> servos_bor;//array que representa os servos da borracharia
    private ArrayList<Cliente> servos_ele;//array que representa os servos da elétrica    
    private ArrayList<Cliente> atendidos;//array com os clientes já atendidos    
    private int qtd_servos_bor; //quantidade de servos na borracharia
    private int qtd_servos_ele; //quantidade de servos na eletrica
    private int qtd_clientes; //quantidade de clientes que vão entrar no sistema
    private int tamanho_fila; //tamanho da fila    
    private float time;//relógio
    
    //parâmetros das distribuições
    private float ele_expo_lambda;
    private float ele_logn_media;
    private float ele_logn_desvpad;
    private float bor_expo_lambda;
    private float bor_logn_media;
    private float bor_logn_desvpad;
    
    //parâmetros das gerções randômicas
    private float ele_probabilidade_pri;//probabilidade de ocorrer um nível de prioridade alta na elétrica   
    
    //objetos que guardam as estatísticas e apresentam na tela
    private Resultado rborracharia;
    private Resultado reletrica;
    
    //barra de progresso
    javax.swing.JProgressBar progresso;
    
    //caminho do arquivo de log
    private String dirlog;
    
    //construtor da classe - devem ser obtidos os parâmetros da tela principal
    public Simulador(int clientes, int tam_fila, int serv_bor, int serv_ele){
        this.clientes = new ArrayList(clientes);
        this.atendidos = new ArrayList(clientes);
        this.fila = new ArrayList(tam_fila);
        this.servos_bor = new ArrayList(serv_bor);
        this.servos_ele = new ArrayList(serv_ele);
        set_time(0);
        set_qtd_servos_bor(serv_bor);
        set_qtd_servos_ele(serv_ele);
        set_qtd_clientes(clientes);
        set_tamanho_fila(tam_fila);
        rborracharia = new Resultado("Borracharia");
        reletrica = new Resultado("Elétrica");
    }
    
    //método principal - irá executar a simulação e atualizar as variáveis
    public boolean simula(){
        
        long tini = System.currentTimeMillis();
        
        gera_eventos();
        
        int[] eventos = new int[qtd_servos_bor+qtd_servos_ele+1];
        
        while (!clientes.isEmpty() | !fila.isEmpty() | !servos_bor.isEmpty() | !servos_ele.isEmpty()){
            
            eventos = prox_ocor(eventos.length);
            
            
            //===========> inicio tratamento de eventos na lista de servos da elétrica
            //percorre parte do vetor correspondente a elétrica em busca de eventos de saída
            for (int i = eventos.length-1; i >= eventos.length - qtd_servos_ele; i--){
                if (eventos[i] == 1){                    
                    set_time(servos_ele.get(i-qtd_servos_bor-1).get_tempo_fim_atendimento());//posição definida por i que é a posção atual, menos a quantidade de posições no array anteriores as posições correspondentes a elétrica(borracharia: qtd_servos_bor e chegada: 1)
                    Cliente pronto = servos_ele.remove(i-qtd_servos_bor-1);//posição definida por i que é a posção atual, menos a quantidade de posições no array anteriores as posições correspondentes a elétrica(borracharia: qtd_servos_bor e chegada: 1)
                    
                    reletrica.add_atendidos();//incrementa atendidos pela elétrica
                    reletrica.add_tempoatendimento(pronto.get_tempo_atendimento());//acumula tempo de atendimento
                    reletrica.add_tempofila(pronto.get_tempo_fila());//acumula tempo da fila
                    
                    atendidos.add(pronto);
                    progresso.setValue(progresso.getValue() + 1);
                }
            }
            //busca clientes para serem inseridos para atendimento na elétrica com base no número de servos disponíveis
            for (int i = 0 ; i < qtd_servos_ele - servos_ele.size(); i++){
                Cliente eleito = proximo_ele(); //elege da fila cliente para ser atendido
                if (eleito != null){
                    eleito.set_tempo_fim_atendimento(get_time());//seta o tempo em que o cliente vai sair do sistema
                    eleito.set_tempo_ini_atendimento(get_time());//seta o tempo de inicio do atendimento
                    eleito.set_tempo_fila(get_time());//seta o tempo que o cliente ficou na fila                    
                    servos_ele.add(eleito); //adiociona cliente ao atendimento                    
                }
            }
            //===========> final tratamento de eventos na lista de servos da elétrica
            
            //===========> inicio tratamento de eventos na lista de servos da borracharia
            //percorre parte do vetor correspondente a borracharia em busca de eventos de saída
            for (int i = eventos.length-1-qtd_servos_ele; i > 0; i--){ 
                if (eventos[i] == 1){                    
                    set_time(servos_bor.get(i-1).get_tempo_fim_atendimento());//posição definida por i que é a posção atual, menos a quantidade de posições no array anteriores as posições correspondentes a borracharia(chegada: 1)
                    Cliente pronto = servos_bor.remove(i-1);//posição definida por i que é a posção atual, menos a quantidade de posições no array anteriores as posições correspondentes a borracharia(chegada: 1)
                    
                    rborracharia.add_atendidos();//incrementa atendidos pela borracharia
                    rborracharia.add_tempoatendimento(pronto.get_tempo_atendimento());//acumula tempo de atendimento
                    rborracharia.add_tempofila(pronto.get_tempo_fila());//acumula tempo da fila
                    
                    atendidos.add(pronto);
                    progresso.setValue(progresso.getValue() + 1);
                }
            }
            //busca clientes para serem inseridos para atendimento na borracharia com base no número de servos disponíveis
            for (int i = 0 ; i < qtd_servos_bor - servos_bor.size(); i++){
                Cliente eleito = proximo_bor(); //elege da fila cliente para ser atendido
                if (eleito != null){
                    eleito.set_tempo_fim_atendimento(get_time());//seta o tempo em que o cliente vai sair do sistema
                    eleito.set_tempo_ini_atendimento(get_time());//seta o tempo de inicio do atendimento
                    eleito.set_tempo_fila(get_time());//seta o tempo que o cliente ficou na fila
                    servos_bor.add(eleito); //adiociona cliente ao atendimento
                }
            }
            //===========> final tratamento de eventos na lista de servos da borracharia
            
            //===========> inicio tratamento de eventos na lista de chegadas
            if (eventos[0] == 1){//se alguém chegou
                
                Cliente novo = clientes.remove(0);
                set_time(novo.get_tempo_chegada());//pega tempo da chegada
                
                switch(novo.get_servico()){//verifica qual o tipo de serviço
                    //caso borracharia
                    case 1: if (servos_bor.size() == qtd_servos_bor){//se todos os servos estão ocupados
                                if (fila.size() < tamanho_fila){//se tem lugar na fila
                                    rborracharia.add_cliente();//incrementa contador de clientes para a borracharia
                                    rborracharia.add_clientefila();//incrementa contador de clientes que entraram na fila
                                    fila.add(novo);//então insere o novo cliente na fila
                                } else { 
                                    //cliente perdido
                                    rborracharia.add_cliente();//incrementa contador de clientes para a borracharia
                                    progresso.setValue(progresso.getValue() + 1);
                                }
                            } else { //tem servos desocupados
                                novo.set_tempo_fim_atendimento(get_time());
                                novo.set_tempo_ini_atendimento(get_time());//seta o tempo de inicio do atendimento
                                rborracharia.add_cliente();//incrementa contador de clientes para a borracharia
                                servos_bor.add(novo); //inicia atendimento do cliente
                            }
                            break;
                    //caso elétrica
                    case 2: if (servos_ele.size() == qtd_servos_ele){//se todos os servos estão ocupados
                                if (fila.size() < tamanho_fila){//se tem lugar na fila
                                    reletrica.add_cliente();//incrementa contador de clientes para a elétrica
                                    reletrica.add_clientefila();//incrementa contador de clientes que entraram na fila
                                    fila.add(novo);//então insere o novo cliente na fila
                                } else { 
                                    //cliente perdido
                                    reletrica.add_cliente();//incrementa contador de clientes para a elétrica
                                    progresso.setValue(progresso.getValue() + 1);
                                }
                            } else { //tem servos desocupados
                                novo.set_tempo_fim_atendimento(get_time());
                                novo.set_tempo_ini_atendimento(get_time());//seta o tempo de inicio do atendimento
                                reletrica.add_cliente();//incrementa contador de clientes para a elétrica
                                servos_ele.add(novo); //inicia atendimento do cliente
                            }
                            break;
                }
            }
            //===========> final tratamento de eventos na lista de chegadas            
        }
        
        //obtem tempo final para calcular tempo de simulação
        long tfim = System.currentTimeMillis();
        
        //atualiza ultimos dados dos resultados
        reletrica.set_time(time, tfim - tini);
        reletrica.gera(this.qtd_servos_ele);
        rborracharia.set_time(time, tfim - tini);            
        rborracharia.gera(this.qtd_servos_bor);
            
        //grava arquivo com os dados dos clientes atendidos;
        try {
            grava_arquivo();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Simulador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Simulador.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //pergunta se quer visualizar o log        
        if ((JOptionPane.showOptionDialog(null, "Simulação realizada com sucesso. Deseja visualizar o log?", "Sucesso", 0, JOptionPane.INFORMATION_MESSAGE, null, null, null)) == 0){
            try {
                Runtime.getRuntime().exec("notepad " + dirlog);//abre o arquivo log
            } catch (Exception p) {
                JOptionPane.showMessageDialog(null, "Não foi possível abrir o arquivo de log: "+p);
            }
        }
        
        //pergunta se quer visualizar os resultados
        if ((JOptionPane.showOptionDialog(null, "Deseja visualizar os resultados?", "Resultados", 0, JOptionPane.INFORMATION_MESSAGE, null, null, null)) == 0){
            try {
                rborracharia.setVisible(true);
                reletrica.setVisible(true);
            } catch (Exception p) {
                JOptionPane.showMessageDialog(null, "Não foi possível abrir o arquivo de log: "+p);
            }
        }
        
        progresso.setValue(0);
             
        return true;
    }
    
    //método que busca próxima ocorrência(saida, chegada, etc...)
    private int[] prox_ocor(int tamanho){
        
        int[] prox = new int[tamanho];
        for (int k = 0; k < tamanho; k++){
            prox[k] = 0;
        }
        float menortime = 99999999;
        
        if (!clientes.isEmpty()){
            menortime = clientes.get(0).get_tempo_chegada();
            prox[0] = 1;
        }
        
        if (!servos_bor.isEmpty()){
            for (int i = 0; i < servos_bor.size(); i++){
                if (servos_bor.get(i).get_tempo_fim_atendimento() < menortime){
                    menortime = servos_bor.get(i).get_tempo_fim_atendimento();
                    prox[i+1] = 1;
                    for (int j = 0; j <= i; j++){
                        prox[j] = 0;
                    }
                } else if ((servos_bor.get(i).get_tempo_fim_atendimento() == menortime)){
                        prox[i+1] = 1;
                    }
            }
        }
        
        if (!servos_ele.isEmpty()){
            for (int i = 0; i < servos_ele.size(); i++){
                if (servos_ele.get(i).get_tempo_fim_atendimento() < menortime){
                    menortime = servos_ele.get(i).get_tempo_fim_atendimento();
                    prox[i+1+qtd_servos_bor] = 1;
                    for (int j = 0; j <= i+qtd_servos_bor; j++){
                        prox[j] = 0;
                    }
                } else if ((servos_ele.get(i).get_tempo_fim_atendimento() == menortime)){
                        prox[i+1+qtd_servos_bor] = 1;
                    }
            }
        }
        
        return prox;
    }

    private Cliente proximo_ele() {
        
        int pos = tamanho_fila; //posição do eleito detro da fila, não pode ser maior que o tamanho da fila, se for é pq nenhum eleito foi encontrado
        boolean achou = false;
        Cliente eleito = null;
        
        if (!fila.isEmpty()){//se a fila não está vazia
            int i = 0;
            while (!achou & i < fila.size()){//enquanto não acha alguém ou não chega ao final da fila
                if (fila.get(i).get_servico() == 2){//testa se o serviço é elétrica
                    if (fila.get(i).get_prioridade() == 1){//testa se a prioridade é 1
                        pos = i; //se sim guarda a posição
                        achou = true; //sinaliza que encontrou o eleito
                    } else { //caso a prioridade seja 2
                        if (i < pos){ //testa se já encontrou uma prioridade 2 antes
                            pos = i; //guarda a menor posição encontrada
                        }
                    }
                }
                i++;
            }
        }
        
        if (pos < tamanho_fila){ //verifica se foi encontrado algum eleito
            eleito = fila.remove(pos);
        }
        return eleito;
    }

    private Cliente proximo_bor() {
        
        int pos = 0;
        boolean achou = false;
        Cliente eleito = null;
        
        if(!fila.isEmpty()){//se fila não está vazia
            while(!achou & pos < fila.size()){//enquanto não achoar ou não chegar ao final da fila
                if (fila.get(pos).get_servico() == 1){//se serviço é borracharia
                    achou = true;//o eleito é p primeiro a ser encontrado
                } else {
                    pos++;
                }
            }
        }
        
        if (achou){
            eleito = fila.remove(pos);
        }
        
        return eleito;
    }

    private void gera_eventos() {
        Random pri_gerador = new Random();//gerador de números aleatórios para definir a prioridade dos serviços da elétrica
        
        //Exponential ele_cheg = new Exponential(this.ele_expo_lambda);
        //Exponential bor_cheg = new Exponential(this.bor_expo_lambda);
        ExponentialDist ele_cheg = new ExponentialDist(this.ele_expo_lambda);
        ExponentialDist bor_cheg = new ExponentialDist(this.bor_expo_lambda);
        Lognormal ele_atend = new Lognormal(this.ele_logn_media, this.ele_logn_desvpad);        
        Lognormal bor_atend = new Lognormal(this.bor_logn_media, this.bor_logn_desvpad);                
        
        Uniform rdm_uniform = new Uniform();
        
        //ele_cheg.setSeed((long) (rdm_uniform.random() * 100000000));        
        //bor_cheg.setSeed((long) (rdm_uniform.random() * 100000000));
        ele_atend.setSeed((long) (rdm_uniform.random() * 100000000));
        bor_atend.setSeed((long) (rdm_uniform.random() * 100000000));        
        
        //int a = 0;
        //int b = 0;        
        //for (int i = 0; i < this.qtd_clientes; i++){
            //a = a + (int) (ele_cheg.inverseCdf(rdm_uniform.random())*1000);
            //b = b + (int) (bor_cheg.inverseCdf(rdm_uniform.random())*1000);
            //a = a + (int) (teste1.inverseF(rdm_uniform.random()));
            //b = b + (int) (teste2.inverseF(rdm_uniform.random()));
        //}
        //System.out.println("Media eletrica: " + a / qtd_clientes);
        //System.out.println("Media borracharia: " + b / qtd_clientes);
        
        float pri_rdm;
        float tempo_ele_cheg = 0;
        float tempo_bor_cheg = 0;
        float ultimo_bor = 0;
        float ultimo_ele = 0;
        int count = 0;
        
        Cliente cliente;
                
        //tempo_ele_cheg = tempo_ele_cheg + (int) (ele_cheg.inverseCdf(rdm_uniform.random())*1000);
        //tempo_bor_cheg = tempo_bor_cheg + (int) (bor_cheg.inverseCdf(rdm_uniform.random())*1000);
        tempo_ele_cheg = tempo_ele_cheg + (int) (ele_cheg.inverseF(rdm_uniform.random()));
        tempo_bor_cheg = tempo_bor_cheg + (int) (bor_cheg.inverseF(rdm_uniform.random()));
        
        while(count < this.qtd_clientes){            
            while ((tempo_ele_cheg < tempo_bor_cheg) & count < this.qtd_clientes){
                pri_rdm = pri_gerador.nextFloat()*100;//gera número aleatório para definir a prioridade de atendimento                
                if (pri_rdm <= this.ele_probabilidade_pri){//se o número gerado for menor que o limite de probabilidade da prioridade mais alta então define como prioridade alta
                    cliente = new Cliente(count,2,1,tempo_ele_cheg,(int)(ele_atend.inverseCdf(rdm_uniform.random())));
                } else {//senão cria com prioridade baixa
                    cliente = new Cliente(count,2,2,tempo_ele_cheg,(int)(ele_atend.inverseCdf(rdm_uniform.random())));
                }
                clientes.add(cliente);//adiciona o novo cliente
                count++;
                //tempo_ele_cheg = tempo_ele_cheg + (int) (ele_cheg.inverseCdf(rdm_uniform.random())*1000);
                ultimo_ele = tempo_ele_cheg;
                tempo_ele_cheg = tempo_ele_cheg + (int) (ele_cheg.inverseF(rdm_uniform.random()));
            }
            if (count < this.qtd_clientes){
                cliente = new Cliente(count,1,tempo_bor_cheg,(int)(bor_atend.inverseCdf(rdm_uniform.random())));
                clientes.add(cliente);//adiciona o novo cliente
                count++;
                //tempo_bor_cheg = tempo_bor_cheg + (int) (bor_cheg.inverseCdf(rdm_uniform.random())*1000);
                ultimo_bor = tempo_bor_cheg;
                tempo_bor_cheg = tempo_bor_cheg + (int) (bor_cheg.inverseF(rdm_uniform.random()));
            }            
        }        
        reletrica.set_tempofinalchegada(ultimo_ele);
        rborracharia.set_tempofinalchegada(ultimo_bor);
    }
    
    public void grava_arquivo() throws FileNotFoundException, IOException{                        
        File arquivo = new File(this.dirlog);
        FileOutputStream fos = new FileOutputStream(arquivo);
        String text = "sequencia;servico;prioridade;chegada;inicio atendimento;fim atendimento;tempo atendimento;tempo fila" + System.getProperty("line.separator");
        fos.write(text.getBytes());
        for(int i = 0; i < atendidos.size(); i++ ){            
            Cliente cl = atendidos.get(i);
            text = cl.get_seq() + ";" + cl.get_servico() + ";" + cl.get_prioridade() + ";" + cl.get_tempo_chegada() + ";" + cl.get_tempo_ini_atendimento() + ";" + cl.get_tempo_fim_atendimento() + ";" + cl.get_tempo_atendimento() + ";" + cl.get_tempo_fila() + System.getProperty("line.separator");
            fos.write(text.getBytes());
        }        
        fos.close();
    }    
    
    @Override
    public void run(){
        simula();
    }
    
    //===============================setters
    private void set_time(float i) {
        this.time = i;
    }

    private void set_qtd_servos_bor(int serv_bor) {
        this.qtd_servos_bor = serv_bor;
    }

    private void set_qtd_servos_ele(int serv_ele) {
        this.qtd_servos_ele = serv_ele;
    }
    
    private void set_qtd_clientes(int cli){
        this.qtd_clientes = cli;
    }

    private void set_tamanho_fila(int tam_fila) {
        this.tamanho_fila = tam_fila;
    }
    
    public void set_ele_expo_lambda(float i){
        this.ele_expo_lambda = i;
    }
    
    public void set_ele_logn_media(float i){
        this.ele_logn_media = i;
    }
    
    public void set_ele_logn_desvpad(float i){
        this.ele_logn_desvpad = i;
    }
    
    public void set_bor_expo_lambda(float i){
        this.bor_expo_lambda = i;
    }
    
    public void set_bor_logn_media(float i){
        this.bor_logn_media = i;
    }
    
    public void set_bor_logn_desvpad(float i){
        this.bor_logn_desvpad = i;
    }
    
    public void set_ele_probabilidade_pri(float p){
        this.ele_probabilidade_pri = p;
    }
    
    public void set_progresso(javax.swing.JProgressBar p){
        this.progresso = p;
        this.progresso.setMinimum(0);
        this.progresso.setMaximum(qtd_clientes);
    }
    
    public void set_dirlog(String dir){
        this.dirlog = dir;
    }
                         
    //===============================getters
    private float get_time(){
        return this.time;
    }
    
    private int get_qtd_servos_bor(){
        return this.qtd_servos_bor;
    }
   
    private int get_qtd_servos_ele(){
        return this.qtd_servos_ele;
    }
  
    private int get_qtd_clientes(){
       return this.qtd_clientes;
    }
   
    private int get_tamanho_fila(){
        return this.tamanho_fila;
    }        
   
    public float get_expo_lambda(){
        return this.ele_expo_lambda;
    }

    public float get_ele_logn_media(){
        return this.ele_logn_media;
    }
 
    public float get_ele_logn_desvpad(){
        return this.ele_logn_desvpad;
    }

    public float get_bor_expo_lambda(){
        return this.bor_expo_lambda;
    }

    public float get_bor_logn_media(){
        return this.bor_logn_media;
    }

    public float get_bor_logn_desvpad(){
        return this.bor_logn_desvpad;
    }

}
