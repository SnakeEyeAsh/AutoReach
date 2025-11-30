package com.javafx.App;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;



import com.javafx.Data.Datos;
import com.javafx.JDBC.Conexion;
import com.javafx.JDBC.seleccion;
import com.javafx.Model.Alquiler;
import com.javafx.Model.Cita;
import com.javafx.Model.Cliente;
import com.javafx.Model.Coche;
import com.javafx.Model.Disponible;

import javafx.application.Platform;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class controladorApp implements Initializable {

    private Pane panelActual;

    private Cliente cliente;

    ///////////////////////////////// CRUD COCHES ////////////
    
    @FXML
    private TextField txtMarcaCompra;

    @FXML
    private TextField txtModeloCompra;

    @FXML
    private TableView<Coche> tblOpcionesCompra;

    @FXML
    private TableColumn<Coche, String> clnMarcaCompra;

    @FXML
    private TableColumn<Coche, String> clnModeloCompra;

    @FXML
    private TableColumn<Coche, String> clnPrecioCompra;
    
    @FXML
    private TableColumn<Coche, String> clnAñoCompra;

    @FXML
    private TableColumn<Coche, String> clnColorCompra;

    
    @FXML
    private TableColumn<Coche, String> clnDiametroCompra;

    @FXML
    private TableColumn<Coche, String> clnInteriorCompra;

    @FXML
    private TableColumn<Coche, String> clnMatIntCompra;
    
    @FXML
    private TableColumn<Coche, String> clnDiseñoRCompra;

    @FXML
    private TableView<Disponible> TblCoincidencias;
    
    @FXML
    private TableColumn<Disponible, String> clnAñoCoche;

    @FXML
    private TableColumn<Disponible, String> clnColorCoche;

    
    @FXML
    private TableColumn<Disponible, String> clnDisponibleCoche;

    @FXML
    private TableColumn<Disponible, String> clnMatriculaCoche;

    @FXML
    private TableColumn<Disponible, String> clnPrecioCoche;   
    
    @FXML
    private Button btnBuscarMatricula;

    @FXML
    private Button btnCancelarMatricula;

    @FXML
    private TextField txtElegirMarca;

    @FXML
    private TextField txtElegirModelo;

    @FXML
    private ListView<Coche> listaCoincidencias;

    @FXML
    private TableView<Coche> TablaCoches;

    @FXML
    private MenuItem opFemenino;

    @FXML
    private MenuItem opMasculino;

    @FXML
    private TableColumn<Coche, String> clnAño;

    @FXML
    private TableColumn<Coche, String> clnBastidor;

    @FXML
    private TableColumn<Coche, String> clnColor;

    @FXML
    private TableColumn<Coche, String> clnMarca;

    @FXML
    private TableColumn<Coche, String> clnModelo;

    @FXML
    private Button btnIrAIngreso;

    @FXML
    private Button btnAceptarIngreso;

    @FXML
    private Button btnCancelarIngresos;

    @FXML
    private Button btnEditarIngreso;

    @FXML
    private Button btnBorrarIngreso;

    @FXML
    private Button btnBorrarCita;

    @FXML
    private Button btnBorrarAlq;

    @FXML
    private Button btnEditarAlquiler;

    @FXML
    private Button btnCancelarAlquiler;

    @FXML
    private Button btnConirmarAlquiler;

    @FXML
    private Button btnAlquiler;

    @FXML
    private Button btnAñadirNuevoCliente;

    @FXML
    private Button btnBuscarCliente;

    
    @FXML
    private Button btnBuscarCompra;

    @FXML
    private Button btnCancelarBCompra;

    @FXML
    private Button btnCancelarCita;

    @FXML
    private Button btnCancelarCompra;

    @FXML
    private Button btnCancelarNuevoCliente;

    @FXML
    private Button btnCancelarSeleccion;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnCitas;

    @FXML
    private Button btnCompra;

    @FXML
    private Button btnCompras;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEditarCliente;

    @FXML
    private Button btnGuardarCita;

    @FXML
    private ImageView btnInicio;

    @FXML
    private Button btnInicioSesion;

    @FXML
    private Button btnIrAInicio;

    @FXML
    private Button btnNuevoCliente;

    @FXML
    private Button btnSeleccionCLiente;

    @FXML
    private DatePicker cldCita;

    @FXML
    private DatePicker cldEntrega;

    @FXML
    private DatePicker cldRecoger;

    @FXML
    private TableColumn<Alquiler, String> clnDNIAlq;

    @FXML
    private TableColumn<Cita, String> clnFecha;

    @FXML
    private TableColumn<Alquiler, String> clnFechaAlq;

    @FXML
    private TableColumn<Alquiler, String> clnHora;

    @FXML
    private TableColumn<Alquiler, String> clnHoraAlq;

    @FXML
    private TableColumn<Alquiler, String> clnMatricula;

    @FXML
    private TableColumn<Alquiler, String> clnNombre;

    @FXML
    private TableColumn<Alquiler, String> clnMatriculaAlq;

    @FXML
    private TableColumn<Cliente, String> clnDniRegistro;

    @FXML
    private TableColumn<Cliente, String> clnNombreRegistro;

    @FXML
    private Label lblAño;

    @FXML
    private Label lblEdad;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblKms;

    @FXML
    private Label lblMarca;

    @FXML
    private Label lblModelo;

    @FXML
    private Label lblNBastidor;

    @FXML
    private Label lblNombre;

    @FXML
    private Label lblNumero;

    @FXML
    private Label lblSexo;

    @FXML
    private Pane paneLogin;

    @FXML
    private Pane panelCitas;

    @FXML
    private AnchorPane panelLoginCliente;

    @FXML
    private AnchorPane panelNuevoCliente;
    /////////////////////////////////////////// PANELES //////////////////////////////////////

    @FXML
    private Pane panelRegistro;

    @FXML
    private Pane panelVentas;

    @FXML
    private Pane pantallaAlquiler;

    @FXML
    private Pane pantallaSesion;

    @FXML
    private Pane panelIngreso;

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    private MenuButton sclColorCompra;

    @FXML
    private MenuButton slcConsumoCompra;

    @FXML
    private SplitMenuButton slcMatCita;

    @FXML
    private MenuButton slcMatriculaLogin;

    @FXML
    private SplitMenuButton splitMenuSexo;

    @FXML
    private ListView<String> tablaDeInformacion;

    @FXML
    private TableView<Cliente> tablaRegistro;

    @FXML
    private TableView<Alquiler> tblAlquiler;

    @FXML
    private TableView<Cita> tblCitas;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtContraseña;

    @FXML
    private TextField txtDireccion;

    @FXML
    private TextField txtDni;

    @FXML
    private TextField txtDniVer;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtFechaNac;

    @FXML
    private TextField txtHoraEntrega;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtNombreVer;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtUser;

    @FXML
    private VBox ventanaLogin;

    
    @FXML
    private TextArea txtAreaCitas;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        Conexion.realizarConexion();
        Platform.runLater(()->{
            Stage s = (Stage)btnIrAInicio.getScene().getWindow();
            s.getIcons().add(new Image("Assets/autoIcon.png"));
        });

        btnIrAInicio.setStyle("-fx-background-color:darkgrey");
        iniciarTablas();
        ocultarTodos();
        desactivarBotones();
        mostrarPanel(paneLogin);
        deshabilitarBotonesCliente();

        cliente = new Cliente("Pepe", "Gonzales", "7526083f", 623162788, "empleado@gmail.com");////////////////////////////////////////////////////////////////

    }

    ////////////////////////////////////////////////////////-----LOGIN------//////////////////////////////////////////////////////////////

    @FXML
    void IniciarSesion(ActionEvent event) {
        if (txtUser.getText().equals(Datos.ADMIN.toString())
                && txtContraseña.getText().equals(Datos.PASSWORD.toString())) {
            mostrarPanel(panelRegistro);
            btnAlquiler.setDisable(false);
            btnIrAInicio.setDisable(false);
            btnCitas.setDisable(false);
            btnCompras.setDisable(false);
            btnCerrarSesion.setDisable(false);
            btnIrAIngreso.setDisable(false);
        }else{
            AlertaFallo.mostrar();
        }
    }

    ////////////////////////////---LOGIN CLIENTE---/////////////////////////////////////////
    
     @FXML
    void crearCliente(ActionEvent event) {
        txtNombreVer.setDisable(true);
        txtDniVer.setDisable(true);
        btnSeleccionCLiente.setDisable(true);
        btnEditarCliente.setDisable(true);
        btnCancelarSeleccion.setDisable(true);

        txtNombre.setDisable(false);
        txtApellidos.setDisable(false);
        txtDireccion.setDisable(false);
        txtTelefono.setDisable(false);
        txtFechaNac.setDisable(false);
        txtDni.setDisable(false);
        txtEmail.setDisable(false);
        btnAñadirNuevoCliente.setDisable(false);
        btnCancelarNuevoCliente.setDisable(false);
        splitMenuSexo.setDisable(false);
    }

    @FXML
    void añadirCliente(ActionEvent event) {
        String nombre = txtNombre.getText();
        String apellidos = txtApellidos.getText();
        String direccion = txtDireccion.getText();
        String telefono = txtTelefono.getText();
        String fechaNac = txtFechaNac.getText();
        String dni = txtDni.getText();
        String email = txtEmail.getText();
        String sexo = splitMenuSexo.getText();
        try{
        Conexion.crearCliente(nombre,apellidos, dni, telefono, email,direccion,fechaNac,sexo);
            Alert alerta = new Alert(AlertType.CONFIRMATION);
            alerta.showAndWait();
        }catch(SQLException error){
            System.out.println(error.getMessage());
        }

    }
     @FXML
    void cancelarNuevoCliente(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }
    /*==================================BUSCAR===========================================*/

    @FXML
    void buscarLogin(ActionEvent event) {
        txtNombreVer.setDisable(false);
        txtDniVer.setDisable(false);
        btnSeleccionCLiente.setDisable(false);
        btnEditarCliente.setDisable(false);
        btnCancelarSeleccion.setDisable(false);

        txtNombre.setDisable(true);
        txtApellidos.setDisable(true);
        txtDireccion.setDisable(true);
        txtTelefono.setDisable(true);
        txtFechaNac.setDisable(true);
        txtDni.setDisable(true);
        txtEmail.setDisable(true);
        btnAñadirNuevoCliente.setDisable(true);
        btnCancelarNuevoCliente.setDisable(true);
        splitMenuSexo.setDisable(true);

    }

    @FXML
    void seleccionarCliente(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void editarCliente(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void cancelarSeleccion(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    //////////////////////////-----ELEGIR SEXO-------/////////////////////////////////

        @FXML
    void elegirFemenino(ActionEvent event) {
        splitMenuSexo.setText(opFemenino.getText());
    }

    @FXML
    void elegirMasculino(ActionEvent event) {
        splitMenuSexo.setText(opMasculino.getText());
    }
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////-----------CITAS-----------------///////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    
    @FXML
    void guardarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void editarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void borrarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();

    }

    @FXML
    void cancelarCita(ActionEvent event) {
        alertaDesarrollo.mostrar();

    }
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////--------Alquiler--------//////////////////////////////////
    /// ////////////////////////////////////////////////////////////////////
    
     @FXML
    void confirmarAlquiler(ActionEvent event) {
        Disponible d = TblCoincidencias.getSelectionModel().getSelectedItem();

        LocalDate fecha = cldRecoger.getValue();
        LocalDate fechaFin = cldEntrega.getValue();

        long dias = ChronoUnit.DAYS.between(fecha, fechaFin);
        double precioTotal = dias * Integer.parseInt(d.getPrecio());
        try{

        Conexion.crearAlquiler(fecha.toString(),
                                fechaFin.toString(),
                                precioTotal+"",
                                 d.getPrecio(),
                                  d.getDisponible(),
                                   1+"",
                                    txtHoraEntrega.getText(),
                                     d.getMatricula(),
                                      cliente.getDni());
        }catch(SQLException e){
            System.out.println("Error al incertar cita desde controlador "+e.getMessage());
        }

    }

    @FXML
    void cancelarAlquiler(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void borrarAlq(ActionEvent event) {
        alertaDesarrollo.mostrar();

    }

    @FXML
    void editarAlq(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }
    @FXML
    void buscarMatricula(ActionEvent event) {

        //TblCoincidencias.getItems().clear();
        String marca = txtElegirMarca.getText();
        String modelo = txtElegirModelo.getText();
        TblCoincidencias.setItems(Conexion.mostrarCoincidencias(marca,modelo));
        clnAñoCoche.setCellValueFactory(new PropertyValueFactory<>("año"));
        clnDisponibleCoche.setCellValueFactory(new PropertyValueFactory<>("Disponible"));
        clnColorCoche.setCellValueFactory(new PropertyValueFactory<>("Color"));
        clnPrecioCoche.setCellValueFactory(new PropertyValueFactory<>("Precio"));
        clnMatriculaCoche.setCellValueFactory(new PropertyValueFactory<>("matricula"));

    }

    @FXML
    void cancelarBusquedaMatricula(ActionEvent event) {
       txtElegirMarca.clear();
       txtElegirModelo.clear();
       TblCoincidencias.getItems().clear();
    }

    /// 
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////-----------COMPRA------------///////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    @FXML
    void efectuarCompra(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pantallaCompra.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Compra");
            stage.initModality(Modality.WINDOW_MODAL); // hace modal
            stage.initOwner(btnCompra.getScene().getWindow()); // define dueño
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            System.out.println(e.getMessage() + "Hola");
        }

    }

    @FXML
    void cancelarCompra(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void BuscarCompra(ActionEvent event) {

    }

    @FXML
    void CancelarBusquedaCompra(ActionEvent event) {

    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////--------INGRESOS-----------------////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @FXML
    void aceptarIngreso(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void cancelarIngreso(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void editarIngreso(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }

    @FXML
    void borrarIngreso(ActionEvent event) {
        alertaDesarrollo.mostrar();
    }
   
    /////////////////////////////////////////////////////////////////////////////////////////////
    /// /////////////////////------SALIR----------------/////////////////////////////////////////
    /// /////////////////////////////////////////////////////////////////////////////////////
    @FXML
    void cerrarSesion(ActionEvent event) {
        Alert alerta = new Alert(AlertType.WARNING);
        alerta.setHeaderText("Saliendo del Programa");
        alerta.setContentText("Seguro que quieres salir del programa");
        alerta.getButtonTypes().addAll(ButtonType.CANCEL);

        Optional<ButtonType> opcion = alerta.showAndWait();

        if (opcion.isPresent() && opcion.get().equals(ButtonType.OK)) {
            Stage escena = (Stage) btnIrAInicio.getScene().getWindow();
            escena.close();
        } else {
            alerta.close();
        }
    }


    /////////////////////////////////////////////////////////// CAMBIAR ENTRE
    /// PANELES /////////////////////////////////////////////
    @FXML
    void irAInicio(ActionEvent event) {
        mostrarPanel(panelRegistro);
        cambiarColores.cambiar(btnIrAInicio, btnAlquiler, btnCompras, btnCitas, btnIrAIngreso);
    }

    @FXML
    void irAalquiler(ActionEvent event) {
        mostrarPanel(pantallaAlquiler);
        cambiarColores.cambiar(btnAlquiler, btnIrAInicio, btnCompras, btnCitas, btnIrAIngreso);
    }

    @FXML
    void irAcitas(ActionEvent event) {
        mostrarPanel(panelCitas);
        cambiarColores.cambiar(btnCitas, btnIrAInicio, btnAlquiler, btnCompras, btnIrAIngreso);
    }

    @FXML
    void irAcompras(ActionEvent event) {
        mostrarPanel(panelVentas);
        cambiarColores.cambiar(btnCompras, btnIrAInicio, btnAlquiler, btnCitas, btnIrAIngreso);
    }

    @FXML
    void irAIngresos(ActionEvent event) {
        mostrarPanel(panelIngreso);
        cambiarColores.cambiar(btnIrAIngreso, btnCompras, btnIrAInicio, btnAlquiler, btnCitas);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////--------    
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /////////////////////////////////////////////////////////////////////////////////////////////////
    /// 
    /// FUNCIONES AUXILIARES
    /// 
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /// 
    private void deshabilitarBotonesCliente(){
        txtNombreVer.setDisable(true);
        txtDniVer.setDisable(true);
        btnSeleccionCLiente.setDisable(true);
        btnEditarCliente.setDisable(true);
        btnCancelarSeleccion.setDisable(true);
        txtNombre.setDisable(true);
        txtApellidos.setDisable(true);
        txtDireccion.setDisable(true);
        txtTelefono.setDisable(true);
        txtFechaNac.setDisable(true);
        txtDni.setDisable(true);
        txtEmail.setDisable(true);
        btnAñadirNuevoCliente.setDisable(true);
        btnCancelarNuevoCliente.setDisable(true);
        splitMenuSexo.setDisable(true);
        
    }

    private void ocultarTodos() {
        panelCitas.setVisible(false);
        panelVentas.setVisible(false);
        pantallaAlquiler.setVisible(false);
        pantallaSesion.setVisible(false);
        panelRegistro.setVisible(false);
        panelIngreso.setVisible(false);
    }

    private void mostrarPanel(Pane panel) {
        if (panelActual != null) {
            panelActual.setVisible(false);
        }
        panel.setVisible(true);
        panel.toFront();
        panelActual = panel;
    }

    private void desactivarBotones() {
        btnAlquiler.setDisable(true);
        btnIrAInicio.setDisable(true);
        btnCitas.setDisable(true);
        btnCompras.setDisable(true);
        btnCerrarSesion.setDisable(true);
        btnIrAIngreso.setDisable(true);
    }

    private void iniciarTablas() {

        tablaRegistro.setItems(Conexion.mostrarClientes());
        clnNombreRegistro.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        clnDniRegistro.setCellValueFactory(new PropertyValueFactory<>("dni"));

        tblCitas.setItems(Conexion.mostrarCitas());
        clnMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        clnFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        clnHora.setCellValueFactory(new PropertyValueFactory<>("hora"));

        tblAlquiler.setItems(Conexion.mostrarAlquiler());
        clnFechaAlq.setCellValueFactory(new PropertyValueFactory<>("fechaDevolucion"));
        clnHoraAlq.setCellValueFactory(new PropertyValueFactory<>("horaDevolucion"));
        clnDNIAlq.setCellValueFactory(new PropertyValueFactory<>("dni"));
        clnMatriculaAlq.setCellValueFactory(new PropertyValueFactory<>("matricula"));

        TablaCoches.setItems(Conexion.mostrarCoches());
        clnMarca.setCellValueFactory(new PropertyValueFactory<>("Marca"));
        clnModelo.setCellValueFactory(new PropertyValueFactory<>("Modelo"));
        clnAño.setCellValueFactory(new PropertyValueFactory<>("Año"));
        clnBastidor.setCellValueFactory(new PropertyValueFactory<>("Bastidor"));
        clnColor.setCellValueFactory(new PropertyValueFactory<>("Color"));

    }

}
