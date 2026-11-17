/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package sistemagames.view;

import sistemagames.conexao.Conexao;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author gs769
 */
public class TelaPrincipal extends javax.swing.JFrame {
    
    private javax.swing.JComboBox<String> cbEquipeInscricao = new javax.swing.JComboBox<>();
    private javax.swing.JComboBox<String> cbCampeonatoInscricao = new javax.swing.JComboBox<>();
    private javax.swing.JTable tabelaInscricoes = new javax.swing.JTable();
    private javax.swing.JButton btnInscreverEquipe = new javax.swing.JButton("Inscrever equipe");
    private javax.swing.JButton btnCancelarInscricao = new javax.swing.JButton("Cancelar inscrição");
    private javax.swing.JButton btnAtualizarInscricoes = new javax.swing.JButton("Atualizar lista");
    private javax.swing.JComboBox<String> cbEquipeCadastroCampeonato = new javax.swing.JComboBox<>();
    private javax.swing.JButton btnCadastrarEquipeCampeonato = new javax.swing.JButton("Cadastrar equipe no campeonato");

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaPrincipal.class.getName());

    /**
     * Creates new form TelaPrincipal
     */
    public TelaPrincipal() {
        initComponents();
        conectarEventos();
        atualizarTudo();
    }

    private void conectarEventos() {
        btnNovaEquipe.addActionListener(e -> novoEditarEquipe(false));
        btnEditarEquipe.addActionListener(e -> novoEditarEquipe(true));
        btnExcluirEquipe.addActionListener(e -> excluirSelecionado(tabelaEquipes, "equipes", "id_equipe", "equipe"));
        btnAtualizarEquipes.addActionListener(e -> carregarEquipes());
        btnVincularJogador.addActionListener(e -> vincularJogador());
        btnDesvincularJogador.addActionListener(e -> desvincularJogador());
        btnAtualizarVinculos.addActionListener(e -> { carregarEquipesCombo(cbEquipe); carregarJogadoresCombo(); carregarJogadoresVinculados(); });
        btnInscreverEquipe.addActionListener(e -> inscreverEquipe());
        btnCancelarInscricao.addActionListener(e -> cancelarInscricao());
        btnAtualizarInscricoes.addActionListener(e -> { carregarEquipesCombo(cbEquipeInscricao); carregarCampeonatosInscricao(); carregarInscricoes(); });
        btnConsultarJogadoresPorEquipe.addActionListener(e -> consultarJogadores(cbEquipeConsultaPorEquipe, tableJogadoresPorEquipe));
        btnAtualizarConsultaJogadoresPorEquipe.addActionListener(e -> { carregarEquipesCombo(cbEquipeConsultaPorEquipe); consultarJogadores(cbEquipeConsultaPorEquipe, tableJogadoresPorEquipe); });
        btnConsultarEquipes.addActionListener(e -> consultarEquipesPorCampeonato());
        btnAtualizarConsultaEquipes.addActionListener(e -> { carregarCampeonatosCombo(); carregarEquipesCompativeis(); consultarEquipesPorCampeonato(); });
        btnCadastrarEquipeCampeonato.addActionListener(e -> inscreverEquipeNaAbaCampeonato());
        jComboBox1.addActionListener(e -> { carregarEquipesCompativeis(); consultarEquipesPorCampeonato(); });
        cbEquipe.addActionListener(e -> carregarJogadoresVinculados());
        cbCampeonatoInscricao.addActionListener(e -> carregarInscricoes());
        cbEquipeConsulta.addActionListener(e -> consultarJogadores(cbEquipeConsulta, tabelaJogadoresPorEquipe));
        cbEquipeConsultaPorEquipe.addActionListener(e -> consultarJogadores(cbEquipeConsultaPorEquipe, tableJogadoresPorEquipe));
        jTabbedPane1.addChangeListener(e -> atualizarAbaSelecionada());
    }

    private void montarAbaInscricoes() {
        JPanel painel = new JPanel(new java.awt.BorderLayout(10, 10));
        
        JPanel topo = new JPanel(new GridLayout(3, 2, 8, 8));
        
        topo.add(new JLabel("Equipe:")); topo.add(cbEquipeInscricao);
        
        topo.add(new JLabel("Campeonato:")); topo.add(cbCampeonatoInscricao);
        
        JPanel botoes = new JPanel();
        
        botoes.add(btnInscreverEquipe); botoes.add(btnCancelarInscricao); 
        
        botoes.add(btnAtualizarInscricoes);
        
        topo.add(new JLabel("Gerenciar inscrições")); topo.add(botoes);
        
        tabelaInscricoes.setModel(new DefaultTableModel(new Object[][]{}, 
                new String[]{
                    "ID", "Equipe", "Campeonato", "Jogo", "Data", "Status"
                }
        ) {
            @Override public boolean isCellEditable(int row, int column) { 
                return false;
            }
        });
        painel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        painel.add(topo, java.awt.BorderLayout.NORTH);
        
        painel.add(new javax.swing.JScrollPane(tabelaInscricoes), java.awt.BorderLayout.CENTER);
        
        jTabbedPane1.removeTabAt(4);
        
        jTabbedPane1.insertTab("Inscrições", null, painel, null, 4);
        
    }

    private void carregarCampeonatosInscricao() {
        String anterior = (String) cbCampeonatoInscricao.getSelectedItem();
        cbCampeonatoInscricao.removeAllItems();
        try (Connection c = Conexao.conectar(); PreparedStatement ps = c.prepareStatement(
                "SELECT id_campeonato,nome FROM campeonatos ORDER BY nome"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cbCampeonatoInscricao.addItem(rs.getInt(1) + " - " + rs.getString(2));
        } catch (Exception ex) { 
            mostrarErro(ex); 
        }
        restaurarSelecao(cbCampeonatoInscricao, anterior);
    }

    private void carregarInscricoes() {
        DefaultTableModel m = (DefaultTableModel) tabelaInscricoes.getModel();
        m.setRowCount(0);
        try (Connection c = Conexao.conectar(); PreparedStatement ps = c.prepareStatement(
                "SELECT i.id_inscricao,e.nome,c.nome,e.jogo,i.data_inscricao,i.status " +
                "FROM inscricoes i JOIN equipes e ON e.id_equipe=i.id_equipe " +
                "JOIN campeonatos c ON c.id_campeonato=i.id_campeonato " +
                "ORDER BY i.id_inscricao DESC"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) m.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getTimestamp(5), rs.getString(6)});
        } catch (Exception ex) { 
            mostrarErro(ex); 
        }
    }

    private void inscreverEquipe() {
        Integer equipe = idSelecionado(cbEquipeInscricao);
        Integer campeonato = idSelecionado(cbCampeonatoInscricao);
        if (equipe == null || campeonato == null) {
            JOptionPane.showMessageDialog(this, "Cadastre e selecione uma equipe e um campeonato.");
            return;
        }
        try (Connection c = Conexao.conectar()) {
            String jogoEquipe = null, jogoCampeonato = null, statusCampeonato = null;
            try (PreparedStatement ps = c.prepareStatement("SELECT jogo FROM equipes WHERE id_equipe=?")) {
                ps.setInt(1, equipe); try (ResultSet rs = ps.executeQuery()) { if (rs.next()) jogoEquipe = rs.getString(1); }
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT jogo,status FROM campeonatos WHERE id_campeonato=?")) {
                ps.setInt(1, campeonato); try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { jogoCampeonato = rs.getString(1); statusCampeonato = rs.getString(2); }
                }
            }
            if (jogoEquipe == null || jogoCampeonato == null) throw new SQLException("Equipe ou campeonato não encontrado.");
            if (!jogoEquipe.trim().equalsIgnoreCase(jogoCampeonato.trim()))
                throw new SQLException("A equipe e o campeonato precisam ser do mesmo jogo.");
            if (!"INSCRICOES_ABERTAS".equals(statusCampeonato))
                throw new SQLException("As inscrições deste campeonato não estão abertas.");
            Integer inscricaoExistente = null;
            String statusInscricao = null;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT id_inscricao,status FROM inscricoes WHERE id_equipe=? AND id_campeonato=?")) {
                ps.setInt(1, equipe); ps.setInt(2, campeonato);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { inscricaoExistente = rs.getInt(1); statusInscricao = rs.getString(2); }
                }
            }
            if (inscricaoExistente != null) {
                if ("CONFIRMADA".equals(statusInscricao))
                    throw new SQLException("Esta equipe já está inscrita neste campeonato.");
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE inscricoes SET status='CONFIRMADA',data_inscricao=CURRENT_TIMESTAMP WHERE id_inscricao=?")) {
                    ps.setInt(1, inscricaoExistente); ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO inscricoes(id_equipe,id_campeonato,status) VALUES(?,?,'CONFIRMADA')")) {
                    ps.setInt(1, equipe); ps.setInt(2, campeonato); ps.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(this, "Equipe inscrita com sucesso!");
            carregarInscricoes();
            consultarEquipesPorCampeonato();
        } catch (java.sql.SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Esta equipe já possui inscrição neste campeonato.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) { mostrarErro(ex); }
    }

    private void cancelarInscricao() {
        int linha = tabelaInscricoes.getSelectedRow();
        if (linha < 0) { 
            JOptionPane.showMessageDialog(this, "Selecione uma inscrição na tabela."); 
            return; 
        }
        int id = Integer.parseInt(tabelaInscricoes.getValueAt(tabelaInscricoes.convertRowIndexToModel(linha), 0).toString());
        if (JOptionPane.showConfirmDialog(this, "Deseja cancelar esta inscrição?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) 
            return;
        try {
            executar("UPDATE inscricoes SET status='CANCELADA' WHERE id_inscricao=?", id);
            carregarInscricoes(); consultarEquipesPorCampeonato();
        } catch (Exception ex) {
            mostrarErro(ex);
        }
    }

    private void atualizarTudo() {
        try {
            carregarJogadores(); 
            
            carregarEquipes(); 
            
            carregarCampeonatos();
            
            carregarEquipesCombo(cbEquipe); 
            
            carregarEquipesCombo(cbEquipeConsulta);
            
            carregarEquipesCombo(cbEquipeConsultaPorEquipe); 
            
            carregarCampeonatosCombo();
            
            carregarJogadoresCombo(); 
            
            carregarJogadoresVinculados();
            
        } catch (RuntimeException ex) { 
            mostrarErro(ex); 
        }
    }

    private void atualizarAbaSelecionada() {
        switch (jTabbedPane1.getSelectedIndex()) {
            
            case 0 -> carregarJogadores();
            
            case 1 -> carregarEquipes();
            
            case 2 -> carregarCampeonatos();
            
            case 3 -> { 
                carregarEquipesCombo(cbEquipe); 
                carregarJogadoresCombo(); 
                carregarJogadoresVinculados(); 
            }
            
            case 4 -> { 
                carregarEquipesCombo(cbEquipeInscricao); 
                carregarCampeonatosInscricao(); 
                carregarInscricoes(); 
            }
            
            case 5 -> { 
                carregarEquipesCombo(cbEquipeConsultaPorEquipe); 
                consultarJogadores(cbEquipeConsultaPorEquipe, tableJogadoresPorEquipe); 
            }
            
            case 6 -> { 
                carregarCampeonatosCombo(); 
                carregarEquipesCompativeis(); 
                consultarEquipesPorCampeonato(); 
            }
            
            default -> { }
        }
    }

    private void mostrarErro(Exception ex) {
        String mensagem = ex.getMessage() == null ? ex.toString() : ex.getMessage();
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void executar(String sql, Object... valores) throws SQLException {
        try (Connection c = Conexao.conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) ps.setObject(i + 1, valores[i]);
            ps.executeUpdate();
        }
    }

    private void preencherTabela(javax.swing.JTable tabela, String sql, int colunas) throws SQLException {
        DefaultTableModel modelo = (DefaultTableModel) tabela.getModel();
        modelo.setRowCount(0);
        try (Connection c = Conexao.conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] linha = new Object[colunas];
                for (int i = 0; i < colunas; i++) linha[i] = rs.getObject(i + 1);
                modelo.addRow(linha);
            }
        }
    }

    private void carregarJogadores() {
        try { 
            preencherTabela(tabelaJogadores, "SELECT id_jogador,nickname,nome_completo,email FROM jogadores ORDER BY id_jogador", 4); 
        }
        catch (Exception ex) { 
            mostrarErro(ex); 
        }
    }
    private void carregarEquipes() {
        try { 
            preencherTabela(tabelaEquipes, "SELECT id_equipe,nome,tag,jogo FROM equipes ORDER BY id_equipe", 4); 
        }
        catch (Exception ex) { 
            mostrarErro(ex); 
        }
        carregarEquipesCombo(cbEquipe); 
        
        carregarEquipesCombo(cbEquipeConsulta); 
        
        carregarEquipesCombo(cbEquipeConsultaPorEquipe);
    }
    private void carregarCampeonatos() {
        try {
            preencherTabela(tabelaCampeonatos, "SELECT id_campeonato,nome,jogo,data_inicio,data_fim,premio FROM campeonatos ORDER BY id_campeonato", 6);
        }
        catch (Exception ex) { 
            mostrarErro(ex);
        }
        
        carregarCampeonatosCombo();
    }

    private void carregarEquipesCombo(JComboBox<String> combo) {
        String anterior = (String) combo.getSelectedItem();
        combo.removeAllItems();
        try (Connection c = Conexao.conectar(); PreparedStatement ps = c.prepareStatement("SELECT id_equipe,nome,tag FROM equipes ORDER BY nome"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) combo.addItem(rs.getInt(1) + " - " + rs.getString(2) + " [" + rs.getString(3) + "]");
        } catch (Exception ex) { mostrarErro(ex); }
        restaurarSelecao(combo, anterior);
    }
    private void carregarCampeonatosCombo() {
        String anterior = (String) jComboBox1.getSelectedItem(); jComboBox1.removeAllItems();
        try (Connection c = Conexao.conectar(); PreparedStatement ps = c.prepareStatement("SELECT id_campeonato,nome FROM campeonatos ORDER BY nome"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) jComboBox1.addItem(rs.getInt(1) + " - " + rs.getString(2));
        } catch (Exception ex) { mostrarErro(ex); }
        restaurarSelecao(jComboBox1, anterior);
    }
    private void carregarJogadoresCombo() {
        String anterior = (String) cbJogador.getSelectedItem(); cbJogador.removeAllItems();
        try (Connection c = Conexao.conectar(); PreparedStatement ps = c.prepareStatement("SELECT id_jogador,nickname FROM jogadores ORDER BY nickname"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) cbJogador.addItem(rs.getInt(1) + " - " + rs.getString(2));
        } catch (Exception ex) { mostrarErro(ex); }
        restaurarSelecao(cbJogador, anterior);
    }
    private void restaurarSelecao(JComboBox<String> combo, String anterior) {
        if (anterior == null) return;
        for (int i=0;i<combo.getItemCount();i++) if (anterior.equals(combo.getItemAt(i))) { combo.setSelectedIndex(i); return; }
    }
    private Integer idSelecionado(JComboBox<String> combo) {
        Object item = combo.getSelectedItem(); if (item == null) return null;
        try { return Integer.valueOf(item.toString().split(" - ",2)[0]); } catch (Exception ex) { return null; }
    }
    private Integer idTabela(javax.swing.JTable tabela) {
        int linha = tabela.getSelectedRow(); 
        if (linha < 0) {
            return null;
        }
        return Integer.valueOf(tabela.getValueAt(tabela.convertRowIndexToModel(linha),0).toString());
    }
    private JPanel formulario(String[] rotulos, JTextField[] campos) {
        JPanel painel = new JPanel(new GridLayout(rotulos.length,2,6,6));
        for (int i=0;i<rotulos.length;i++) { 
            painel.add(new JLabel(rotulos[i])); 
            painel.add(campos[i]); 
        }
        return painel;
    }
    private JTextField campo(String valor) { 
        return new JTextField(valor == null ? "" : valor, 18); 
    }

    private void novoEditarJogador(boolean editar) {
        Integer id = editar ? idTabela(tabelaJogadores) : null;
        
        if (editar && id == null) { 
            JOptionPane.showMessageDialog(this,"Selecione um jogador na tabela."); 
            return; 
        }
        String nick0="", nome0="", email0="";
        if (editar)
            try (Connection c=Conexao.conectar(); 
                    PreparedStatement ps=c.prepareStatement("SELECT nickname,nome_completo,email FROM jogadores WHERE id_jogador=?")) {
            ps.setInt(1,id); 
            try(ResultSet rs=ps.executeQuery()){ 
                if(rs.next()){
                    nick0=rs.getString(1);
                    nome0=rs.getString(2);
                    email0=rs.getString(3);
                } 
            }
        } 
            catch(Exception ex){
                mostrarErro(ex);
                return;
            }
        JTextField nick=campo(nick0), nome=campo(nome0), email=campo(email0);
        
        if(JOptionPane.showConfirmDialog(this,formulario(new String[]{
            "Nickname:","Nome completo:","E-mail:"
        },new JTextField[]{nick,nome,email}), editar?"Editar jogador":"Novo jogador",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)
            return;
        
        if(nick.getText().isBlank()||nome.getText().isBlank()||email.getText().isBlank()){
            JOptionPane.showMessageDialog(this,"Preencha todos os campos.");
            return;
        }
        try { 
            if(editar) executar("UPDATE jogadores SET nickname=?,nome_completo=?,email=? WHERE id_jogador=?",
                    nick.getText().trim(),nome.getText().trim(),
                    email.getText().trim(),
                    id);
              else executar("INSERT INTO jogadores(nickname,nome_completo,email) VALUES(?,?,?)",
                      nick.getText().trim(),nome.getText().trim(),
                      email.getText().trim());
            carregarJogadores(); carregarJogadoresCombo();
        } 
        catch(Exception ex){
            mostrarErro(ex);
        }
    }

    private void novoEditarEquipe(boolean editar) {
        Integer id=editar?idTabela(tabelaEquipes):null; 
        if(editar&&id==null){
            JOptionPane.showMessageDialog(this,"Selecione uma equipe na tabela.");
            return;
        }
        String nome0="",tag0="",jogo0="";
        if(editar)
            try(Connection c=Conexao.conectar();
                    PreparedStatement ps=c.prepareStatement("SELECT nome,tag,jogo FROM equipes WHERE id_equipe=?")){
                ps.setInt(1,id);
                try(ResultSet rs=ps.executeQuery()){
                    if(rs.next()){
                        nome0=rs.getString(1);
                        tag0=rs.getString(2);
                        jogo0=rs.getString(3);
                    }
                }
            }
            catch(Exception ex){
                mostrarErro(ex);
                return;
            }
                JTextField nome=campo(nome0),
                tag=campo(tag0),
                jogo=campo(jogo0);
                
        if(JOptionPane.showConfirmDialog(this,formulario(new String[]{
            "Nome:","Tag:","Jogo:"
        },new JTextField[]{
            nome,
            tag,
            jogo
        }
        ),editar?"Editar equipe":"Nova equipe",
        JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)
            return
                    ;
        if(nome.getText().isBlank()||tag.getText().isBlank()||jogo.getText().isBlank()){
            JOptionPane.showMessageDialog(this,"Preencha todos os campos.");
            return;
        }
        try{
            if(editar)executar("UPDATE equipes SET nome=?,tag=?,jogo=? WHERE id_equipe=?",
                    nome.getText().trim(),
                    tag.getText().trim(),
                    jogo.getText().trim(),
                    id);
            else executar("INSERT INTO equipes(nome,tag,jogo) VALUES(?,?,?)",
                    nome.getText().trim(),
                    tag.getText().trim(),
                    jogo.getText().trim());
            carregarEquipes();
        }
        catch(Exception ex){
            mostrarErro(ex);
        }
    }

    private void novoEditarCampeonato(boolean editar) {
        Integer id=editar?idTabela(tabelaCampeonatos):null;
        if(editar&&id==null){
            JOptionPane.showMessageDialog(this,"Selecione um campeonato na tabela.");
            return;
        }
        String[] v={"","","",LocalDate.now().toString(),LocalDate.now().plusDays(1).toString(),"0.00"};
        
        if(editar)
            try(Connection c=Conexao.conectar();
                    PreparedStatement ps=c.prepareStatement("SELECT nome,jogo,status,data_inicio,data_fim,premio FROM campeonatos WHERE id_campeonato=?")){
                ps.setInt(1,id);
                try(ResultSet rs=ps.executeQuery()){
                    if(rs.next()){
                        v[0]=rs.getString(1);
                        v[1]=rs.getString(2);
                        v[2]=rs.getString(3);
                        v[3]=rs.getDate(4).toString();
                        v[4]=rs.getDate(5).toString();
                        v[5]=rs.getBigDecimal(6).toString();
                    }
                }
            }
            catch(Exception ex){
                mostrarErro(ex);
                return;
            }
        JTextField nome=campo(v[0]),jogo=campo(v[1]),inicio=campo(v[3]),fim=campo(v[4]),premio=campo(v[5]);
        
        JComboBox<String> status=new JComboBox<>(new String[]{"INSCRICOES_ABERTAS","EM_ANDAMENTO","ENCERRADO"});
        
        status.setSelectedItem(v[2].isBlank()?"INSCRICOES_ABERTAS":v[2]);
        
        JPanel painel=new JPanel(new GridLayout(6,2,6,6));
        
        String[] labs={"Nome:","Jogo:","Status:","Data início (AAAA-MM-DD):","Data fim (AAAA-MM-DD):","Prêmio:"};
        
        Object[] comps={nome,jogo,status,inicio,fim,premio};
        
        for(int i=0;i<labs.length;i++){
            painel.add(new JLabel(labs[i]));
            painel.add((java.awt.Component)comps[i]);
        }
        
        if(JOptionPane.showConfirmDialog(this,painel,editar?"Editar campeonato":"Novo campeonato",JOptionPane.OK_CANCEL_OPTION)!=JOptionPane.OK_OPTION)
            return;
        try{
            LocalDate di=LocalDate.parse(inicio.getText().trim()),df=LocalDate.parse(fim.getText().trim());BigDecimal pr=new BigDecimal(premio.getText().trim().replace(',','.'));
            if(nome.getText().isBlank()||jogo.getText().isBlank()||df.isBefore(di)||pr.signum()<0)
                throw new IllegalArgumentException("Verifique os campos e as datas do campeonato.");
            if(editar)executar("UPDATE campeonatos SET nome=?,jogo=?,status=?,data_inicio=?,data_fim=?,premio=? WHERE id_campeonato=?",
                    nome.getText().trim(),
                    jogo.getText().trim(),
                    status.getSelectedItem(),
                    Date.valueOf(di),
                    Date.valueOf(df),
                    pr,
                    id
            );
            else executar("INSERT INTO campeonatos(nome,jogo,status,data_inicio,data_fim,premio) VALUES(?,?,?,?,?,?)",
                    nome.getText().trim(),
                    jogo.getText().trim(),
                    status.getSelectedItem(),
                    Date.valueOf(di),
                    Date.valueOf(df),
                    pr
            );
            carregarCampeonatos();
        }catch(Exception ex){
            mostrarErro(ex);
        }
    }

    private void excluirSelecionado(javax.swing.JTable tabela,String tabelaSql,String chave,String rotulo){
        Integer id=idTabela(tabela);
        if(id==null){
            JOptionPane.showMessageDialog(this,"Selecione um registro na tabela.");
            return;
        }if(JOptionPane.showConfirmDialog(this,"Confirma a exclusão deste registro?","Confirmar exclusão",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)
            return;
        try{executar("DELETE FROM "+tabelaSql+" WHERE "+chave+"=?",id);
            if(tabela==tabelaJogadores){
                carregarJogadores();
                carregarJogadoresCombo();
            }else if(tabela==tabelaEquipes)carregarEquipes();else carregarCampeonatos();
        }catch(Exception ex){
            mostrarErro(ex);
        }
    }

    private void vincularJogador(){
        Integer equipe=idSelecionado(cbEquipe),
                jogador=idSelecionado(cbJogador);
        if(equipe==null||jogador==null){JOptionPane.showMessageDialog(this,"Selecione uma equipe e um jogador.");
        return;}
        try {
            Integer equipeAtual = null;
        try (Connection c = Conexao.conectar(); PreparedStatement ps = c.prepareStatement("SELECT id_equipe FROM jogadores WHERE id_jogador=?")) {
            ps.setInt(1, jogador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { 
                    int atual = rs.getInt(1); 
                    if (!rs.wasNull()) equipeAtual = atual; 
                } 
            }
        }
        if (equipe.equals(equipeAtual)) { 
            JOptionPane.showMessageDialog(this, "Este jogador já pertence a esta equipe."); 
            return; 
        }
        if (equipeAtual != null && JOptionPane.showConfirmDialog(this, "O jogador já pertence a outra equipe. Deseja transferi-lo?", "Confirmar transferência", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) 
            return;
        
        executar("UPDATE jogadores SET id_equipe=? WHERE id_jogador=?",equipe,jogador);
        
        carregarJogadoresVinculados(); 
        
        carregarJogadoresCombo();
        
        JOptionPane.showMessageDialog(this, "Jogador vinculado com sucesso!");
        
    } 
        catch(Exception ex){
            mostrarErro(ex);
        }
    }
        private void carregarJogadoresVinculados(){
            Integer equipe=idSelecionado(cbEquipe);
            try{DefaultTableModel m=(DefaultTableModel)tabelaJogadoresVinculados.getModel();
            m.setRowCount(0);
            if(equipe==null)return;
            try(Connection c=Conexao.conectar();
                    PreparedStatement ps=c.prepareStatement("SELECT id_jogador,nickname,nome_completo FROM jogadores WHERE id_equipe=? ORDER BY nickname")){
                ps.setInt(1,equipe);
                try(ResultSet rs=ps.executeQuery()){
                    while(rs.next())m.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3)
                    });
                }
            }
            }catch(Exception ex){
                mostrarErro(ex);
            }
        }
    private void desvincularJogador(){Integer id=idTabela(tabelaJogadoresVinculados);
    if(id==null){
        JOptionPane.showMessageDialog(this,"Selecione um jogador vinculado na tabela.");
        return;
    }
        try{executar("UPDATE jogadores SET id_equipe=NULL WHERE id_jogador=?",id);
        carregarJogadoresVinculados();
        carregarJogadoresCombo()
                ;
        }catch(Exception ex){
            mostrarErro(ex);
        }
    }
        private void consultarJogadores(JComboBox<String> combo,javax.swing.JTable tabela){
            Integer equipe=idSelecionado(combo);
            DefaultTableModel m=(DefaultTableModel)tabela.getModel();
            m.setRowCount(0);
            if(equipe==null)return;
            try(Connection c=Conexao.conectar();
                    PreparedStatement ps=c.prepareStatement("SELECT id_jogador,nickname,nome_completo,email FROM jogadores WHERE id_equipe=? ORDER BY nickname")){
                ps.setInt(1,equipe);
                try(ResultSet rs=ps.executeQuery()){
                    while(rs.next())m.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4)}
                    );
                }
            }catch(Exception ex){mostrarErro(ex);}
        }
        private void carregarEquipesCompativeis() {
            String anterior = (String) cbEquipeCadastroCampeonato.getSelectedItem();
            cbEquipeCadastroCampeonato.removeAllItems();
            Integer campeonato = idSelecionado(jComboBox1);
            if (campeonato == null) return;
            try (Connection c = Conexao.conectar();
                 PreparedStatement ps = c.prepareStatement(
                     "SELECT e.id_equipe,e.nome,e.tag,e.jogo FROM equipes e " +
                     "JOIN campeonatos ca ON UPPER(TRIM(ca.jogo))=UPPER(TRIM(e.jogo)) " +
                     "WHERE ca.id_campeonato=? ORDER BY e.nome")) {
                ps.setInt(1, campeonato);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) cbEquipeCadastroCampeonato.addItem(
                        rs.getInt("id_equipe") + " - " + rs.getString("nome") +
                        " [" + rs.getString("tag") + "] - " + rs.getString("jogo"));
                }
            } catch (Exception ex) { mostrarErro(ex); }
            restaurarSelecao(cbEquipeCadastroCampeonato, anterior);
        }

        private void inscreverEquipeNaAbaCampeonato() {
            Integer equipe = idSelecionado(cbEquipeCadastroCampeonato);
            Integer campeonato = idSelecionado(jComboBox1);
            if (equipe == null || campeonato == null) {
                JOptionPane.showMessageDialog(this,
                    "Selecione um campeonato e uma equipe compatível com o jogo dele.");
                return;
            }
            
            // Reutiliza a mesma validação de jogo, status e duplicidade da tela Inscrições.
            carregarEquipesCombo(cbEquipeInscricao);
            carregarCampeonatosInscricao();
            String equipeSelecionada = cbEquipeCadastroCampeonato.getSelectedItem().toString();
            String campeonatoSelecionado = jComboBox1.getSelectedItem().toString();
            for (int i = 0; i < cbEquipeInscricao.getItemCount(); i++) {
                if (cbEquipeInscricao.getItemAt(i).startsWith(equipe + " - ")) {
                    cbEquipeInscricao.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < cbCampeonatoInscricao.getItemCount(); i++) {
                if (cbCampeonatoInscricao.getItemAt(i).startsWith(campeonato + " - ")) {
                    cbCampeonatoInscricao.setSelectedIndex(i);
                    break;
                }
            }
            inscreverEquipe();
            carregarEquipesCompativeis();
            consultarEquipesPorCampeonato();
        }

        /** Exibe todos os campeonatos e, em cada um, todas as equipes com inscrição confirmada.
         * Campeonatos sem equipes também aparecem, para que nenhum campeonato fique escondido.
         */
        private void consultarEquipesPorCampeonato() {
            DefaultTableModel modelo = (DefaultTableModel) tabelaEquipesPorCampeonato.getModel();
            modelo.setRowCount(0);
            String sql = "SELECT c.id_campeonato, c.nome, c.jogo, c.data_inicio, c.data_fim, c.status, " +
                         "e.nome, e.tag, i.status " +
                         "FROM campeonatos c " +
                         "LEFT JOIN inscricoes i ON i.id_campeonato=c.id_campeonato AND i.status='CONFIRMADA' " +
                         "LEFT JOIN equipes e ON e.id_equipe=i.id_equipe " +
                         "ORDER BY c.data_inicio DESC, c.nome, e.nome";
            try (Connection conexao = Conexao.conectar();
                 PreparedStatement ps = conexao.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String equipe = rs.getString(7);
                    modelo.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getDate(4), rs.getDate(5), rs.getString(6),
                        equipe == null ? "Ainda sem equipes" : equipe,
                        equipe == null ? "-" : rs.getString(8),
                        equipe == null ? "Sem inscrições" : rs.getString(9)
                    });
                }
            } catch (Exception ex) {
                mostrarErro(ex);
            }
        }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        lblTitulo = new javax.swing.JLabel();
        lblDescricao = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnNovoJogador = new javax.swing.JButton();
        btnEditarJogador = new javax.swing.JButton();
        btnExcluirJogador = new javax.swing.JButton();
        btnAtualizarJogadores = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelaJogadores = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblCadastroEquipes = new javax.swing.JLabel();
        btnNovaEquipe = new javax.swing.JButton();
        btnEditarEquipe = new javax.swing.JButton();
        btnExcluirEquipe = new javax.swing.JButton();
        btnAtualizarEquipes = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelaEquipes = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        lblCadastroCampeonatos = new javax.swing.JLabel();
        btnNovoCampeonato = new javax.swing.JButton();
        btnEditarCampeonato = new javax.swing.JButton();
        btnExcluirCampeonato = new javax.swing.JButton();
        btnAtualizarCampeonatos = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelaCampeonatos = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cbEquipe = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cbJogador = new javax.swing.JComboBox<>();
        btnVincularJogador = new javax.swing.JButton();
        btnDesvincularJogador = new javax.swing.JButton();
        btnAtualizarVinculos = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabelaJogadoresVinculados = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cbEquipeConsulta = new javax.swing.JComboBox<>();
        btnConsultarJogadores = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabelaJogadoresPorEquipe = new javax.swing.JTable();
        btnAtualizarConsultaJogadores = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        lblTituloConsultaJogadores = new javax.swing.JLabel();
        lblEquipeConsulta = new javax.swing.JLabel();
        cbEquipeConsultaPorEquipe = new javax.swing.JComboBox<>();
        btnConsultarJogadoresPorEquipe = new javax.swing.JButton();
        btnAtualizarConsultaJogadoresPorEquipe = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tableJogadoresPorEquipe = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        lblTituloConsultaEquipes = new javax.swing.JLabel();
        lblCampeonatoConsulta = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        btnConsultarEquipes = new javax.swing.JButton();
        btnAtualizarConsultaEquipes = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        tabelaEquipesPorCampeonato = new javax.swing.JTable();

        jMenu1.setText("jMenu1");

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitulo.setBackground(new java.awt.Color(0, 0, 0));
        lblTitulo.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTitulo.setText("Sistema Games");

        lblDescricao.setText("Gerenciamento de jogadores. equipes e campeonatos");

        jLabel2.setText("Cadastro Simples de jogadores");

        btnNovoJogador.setText("Novo Jogador");
        btnNovoJogador.addActionListener(this::btnNovoJogadorActionPerformed);

        btnEditarJogador.setText("Editar");
        btnEditarJogador.addActionListener(this::btnEditarJogadorActionPerformed);

        btnExcluirJogador.setText("Excluir");
        btnExcluirJogador.addActionListener(this::btnExcluirJogadorActionPerformed);

        btnAtualizarJogadores.setText("Atualizar Lista");
        btnAtualizarJogadores.addActionListener(this::btnAtualizarJogadoresActionPerformed);

        tabelaJogadores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nickname", "Nome completo", "E-mail"
            }
        ));
        jScrollPane2.setViewportView(tabelaJogadores);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnNovoJogador)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditarJogador)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluirJogador)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAtualizarJogadores))
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovoJogador)
                    .addComponent(btnEditarJogador)
                    .addComponent(btnExcluirJogador)
                    .addComponent(btnAtualizarJogadores))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Jogadores", jPanel1);

        lblCadastroEquipes.setText("Cadastro simples de equipes");

        btnNovaEquipe.setText("Nova equipe");

        btnEditarEquipe.setText("Nova equipe");

        btnExcluirEquipe.setText("Excluir");

        btnAtualizarEquipes.setText("Atualizar lista");

        tabelaEquipes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Tag", "Jogo"
            }
        ));
        jScrollPane1.setViewportView(tabelaEquipes);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnNovaEquipe)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditarEquipe)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluirEquipe)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAtualizarEquipes))
                    .addComponent(lblCadastroEquipes)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lblCadastroEquipes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovaEquipe)
                    .addComponent(btnEditarEquipe)
                    .addComponent(btnExcluirEquipe)
                    .addComponent(btnAtualizarEquipes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Equipes", jPanel2);

        lblCadastroCampeonatos.setText("Cadastro de campeonatos");

        btnNovoCampeonato.setText("Novo Campeonato");
        btnNovoCampeonato.addActionListener(this::btnNovoCampeonatoActionPerformed);

        btnEditarCampeonato.setText("Editar");
        btnEditarCampeonato.addActionListener(this::btnEditarCampeonatoActionPerformed);

        btnExcluirCampeonato.setText("Excluir");
        btnExcluirCampeonato.addActionListener(this::btnExcluirCampeonatoActionPerformed);

        btnAtualizarCampeonatos.setText("Atualizar lista");
        btnAtualizarCampeonatos.addActionListener(this::btnAtualizarCampeonatosActionPerformed);

        tabelaCampeonatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Jogo", "Data início", "Data fim", "Prêmio"
            }
        ));
        jScrollPane3.setViewportView(tabelaCampeonatos);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(btnNovoCampeonato)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditarCampeonato)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluirCampeonato)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAtualizarCampeonatos))
                    .addComponent(lblCadastroCampeonatos)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lblCadastroCampeonatos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovoCampeonato)
                    .addComponent(btnEditarCampeonato)
                    .addComponent(btnExcluirCampeonato)
                    .addComponent(btnAtualizarCampeonatos))
                .addGap(60, 60, 60)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Campeonatos", jPanel3);

        jLabel3.setText("Gerenciamento de jogadores por equipe");

        jLabel4.setText("Selecionar equipe:");

        cbEquipe.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbEquipe.addActionListener(this::cbEquipeActionPerformed);

        jLabel5.setText("Selecionar jogador:");

        cbJogador.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnVincularJogador.setText("Vincular jogador");

        btnDesvincularJogador.setText("Desvincular jogador");
        btnDesvincularJogador.addActionListener(this::btnDesvincularJogadorActionPerformed);

        btnAtualizarVinculos.setText("Atualizar lista");
        btnAtualizarVinculos.addActionListener(this::btnAtualizarVinculosActionPerformed);

        tabelaJogadoresVinculados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Nickname", "Nome completo"
            }
        ));
        jScrollPane4.setViewportView(tabelaJogadoresVinculados);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(cbJogador, javax.swing.GroupLayout.Alignment.LEADING, 0, 112, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(cbEquipe, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(btnVincularJogador)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDesvincularJogador)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAtualizarVinculos))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbEquipe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbJogador, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVincularJogador)
                    .addComponent(btnDesvincularJogador)
                    .addComponent(btnAtualizarVinculos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        jTabbedPane1.addTab("Vincular Jogador", jPanel4);

        jLabel6.setText("Cadastrar equipe para campeonato");

        jLabel7.setText("Selecionar equipe:");

        cbEquipeConsulta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnConsultarJogadores.setText("Cadastrar equipe");
        btnConsultarJogadores.addActionListener(this::btnConsultarJogadoresActionPerformed);

        tabelaJogadoresPorEquipe.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nickname", "Nome Completo", "E-mail"
            }
        ));
        jScrollPane5.setViewportView(tabelaJogadoresPorEquipe);

        btnAtualizarConsultaJogadores.setText("Atualizar lista");
        btnAtualizarConsultaJogadores.addActionListener(this::btnAtualizarConsultaJogadoresActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbEquipeConsulta, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(btnConsultarJogadores)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAtualizarConsultaJogadores)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbEquipeConsulta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConsultarJogadores)
                    .addComponent(btnAtualizarConsultaJogadores))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Incrições", jPanel5);

        lblTituloConsultaJogadores.setText("Consulta de jogadores por equipe");

        lblEquipeConsulta.setText("Selecionar equipe:");

        cbEquipeConsultaPorEquipe.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnConsultarJogadoresPorEquipe.setText("Consultar jogadores");

        btnAtualizarConsultaJogadoresPorEquipe.setText("Atualizar lista");

        tableJogadoresPorEquipe.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Nickname", "Nome completo", "E-mail"
            }
        ));
        jScrollPane6.setViewportView(tableJogadoresPorEquipe);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(btnConsultarJogadoresPorEquipe)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAtualizarConsultaJogadoresPorEquipe))
                    .addComponent(lblTituloConsultaJogadores)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbEquipeConsultaPorEquipe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEquipeConsulta)))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(lblTituloConsultaJogadores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEquipeConsulta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbEquipeConsultaPorEquipe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConsultarJogadoresPorEquipe)
                    .addComponent(btnAtualizarConsultaJogadoresPorEquipe))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Jogadores por Equipe", jPanel6);

        lblTituloConsultaEquipes.setText("Consulta de equipes por campeonato");

        lblCampeonatoConsulta.setText("Selecionar campeonato:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnConsultarEquipes.setText("Consultar equipes");

        btnAtualizarConsultaEquipes.setText("Atualizar lista");

        tabelaEquipesPorCampeonato.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID equipe", "Nome", "Tag", "Jogo"
            }
        ));
        jScrollPane7.setViewportView(tabelaEquipesPorCampeonato);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloConsultaEquipes)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(btnConsultarEquipes)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAtualizarConsultaEquipes))
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblCampeonatoConsulta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(lblTituloConsultaEquipes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCampeonatoConsulta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConsultarEquipes)
                    .addComponent(btnAtualizarConsultaEquipes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Equipe por campeonatos", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 759, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDescricao)
                    .addComponent(lblTitulo))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDescricao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNovoJogadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoJogadorActionPerformed
        novoEditarJogador(false);
    }
//GEN-LAST:event_btnNovoJogadorActionPerformed

    private void btnEditarJogadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarJogadorActionPerformed
        novoEditarJogador(true);
    }
//GEN-LAST:event_btnEditarJogadorActionPerformed

    private void btnExcluirJogadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirJogadorActionPerformed
        excluirSelecionado(tabelaJogadores, "jogadores", "id_jogador", "jogador");
    }
//GEN-LAST:event_btnExcluirJogadorActionPerformed

    private void btnAtualizarJogadoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarJogadoresActionPerformed
        carregarJogadores();
    }
//GEN-LAST:event_btnAtualizarJogadoresActionPerformed

    private void btnNovoCampeonatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoCampeonatoActionPerformed
        novoEditarCampeonato(false);
    }
//GEN-LAST:event_btnNovoCampeonatoActionPerformed

    private void btnEditarCampeonatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarCampeonatoActionPerformed
        novoEditarCampeonato(true);
    }
//GEN-LAST:event_btnEditarCampeonatoActionPerformed

    private void btnExcluirCampeonatoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirCampeonatoActionPerformed
        excluirSelecionado(tabelaCampeonatos, "campeonatos", "id_campeonato", "campeonato");
    }
//GEN-LAST:event_btnExcluirCampeonatoActionPerformed

    private void btnAtualizarCampeonatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarCampeonatosActionPerformed
        carregarCampeonatos();
    }
//GEN-LAST:event_btnAtualizarCampeonatosActionPerformed

    private void cbEquipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbEquipeActionPerformed
        carregarJogadoresVinculados();
    }
//GEN-LAST:event_cbEquipeActionPerformed

    private void btnDesvincularJogadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesvincularJogadorActionPerformed
        desvincularJogador();
    }
//GEN-LAST:event_btnDesvincularJogadorActionPerformed

    private void btnAtualizarVinculosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarVinculosActionPerformed
        carregarEquipesCombo(cbEquipe); carregarJogadoresCombo(); carregarJogadoresVinculados();
    }
//GEN-LAST:event_btnAtualizarVinculosActionPerformed

    private void btnConsultarJogadoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarJogadoresActionPerformed
        consultarJogadores(cbEquipeConsulta, tabelaJogadoresPorEquipe);
    }
//GEN-LAST:event_btnConsultarJogadoresActionPerformed

    private void btnAtualizarConsultaJogadoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarConsultaJogadoresActionPerformed
        carregarEquipesCombo(cbEquipeConsulta); consultarJogadores(cbEquipeConsulta, tabelaJogadoresPorEquipe);
    }
//GEN-LAST:event_btnAtualizarConsultaJogadoresActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizarCampeonatos;
    private javax.swing.JButton btnAtualizarConsultaEquipes;
    private javax.swing.JButton btnAtualizarConsultaJogadores;
    private javax.swing.JButton btnAtualizarConsultaJogadoresPorEquipe;
    private javax.swing.JButton btnAtualizarEquipes;
    private javax.swing.JButton btnAtualizarJogadores;
    private javax.swing.JButton btnAtualizarVinculos;
    private javax.swing.JButton btnConsultarEquipes;
    private javax.swing.JButton btnConsultarJogadores;
    private javax.swing.JButton btnConsultarJogadoresPorEquipe;
    private javax.swing.JButton btnDesvincularJogador;
    private javax.swing.JButton btnEditarCampeonato;
    private javax.swing.JButton btnEditarEquipe;
    private javax.swing.JButton btnEditarJogador;
    private javax.swing.JButton btnExcluirCampeonato;
    private javax.swing.JButton btnExcluirEquipe;
    private javax.swing.JButton btnExcluirJogador;
    private javax.swing.JButton btnNovaEquipe;
    private javax.swing.JButton btnNovoCampeonato;
    private javax.swing.JButton btnNovoJogador;
    private javax.swing.JButton btnVincularJogador;
    private javax.swing.JComboBox<String> cbEquipe;
    private javax.swing.JComboBox<String> cbEquipeConsulta;
    private javax.swing.JComboBox<String> cbEquipeConsultaPorEquipe;
    private javax.swing.JComboBox<String> cbJogador;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblCadastroCampeonatos;
    private javax.swing.JLabel lblCadastroEquipes;
    private javax.swing.JLabel lblCampeonatoConsulta;
    private javax.swing.JLabel lblDescricao;
    private javax.swing.JLabel lblEquipeConsulta;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTituloConsultaEquipes;
    private javax.swing.JLabel lblTituloConsultaJogadores;
    private javax.swing.JTable tabelaCampeonatos;
    private javax.swing.JTable tabelaEquipes;
    private javax.swing.JTable tabelaEquipesPorCampeonato;
    private javax.swing.JTable tabelaJogadores;
    private javax.swing.JTable tabelaJogadoresPorEquipe;
    private javax.swing.JTable tabelaJogadoresVinculados;
    private javax.swing.JTable tableJogadoresPorEquipe;
    // End of variables declaration//GEN-END:variables
}
