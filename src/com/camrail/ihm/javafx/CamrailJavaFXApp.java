package com.camrail.ihm.javafx;

import com.camrail.core.model.ClasseVoyage;
import com.camrail.core.model.Siege;
import com.camrail.core.model.Trajet;
import com.camrail.core.model.Reservation;
import com.camrail.persistance.TicketWriter;
import com.camrail.ihm.javafx.DataStoreFX.City;
import com.camrail.ihm.javafx.DataStoreFX.Promo;
import com.camrail.ihm.javafx.DataStoreFX.Review;
import com.camrail.ihm.javafx.DataStoreFX.User;
import com.camrail.ihm.javafx.DataStoreFX.ReservationFX;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CamrailJavaFXApp extends Application {

    // --- APPLICATION STATE ---
    private User currentUser = null;
    private final StringProperty fromCity = new SimpleStringProperty("");
    private final StringProperty toCity = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> travelDate = new SimpleObjectProperty<>(LocalDate.now().plusDays(1));
    private final IntegerProperty passengerCount = new SimpleIntegerProperty(1);
    private final StringProperty selectedClass = new SimpleStringProperty("ECONOMIQUE");
    
    private Trajet selectedTrain = null;
    private int selectedSeatNumber = -1;
    private double finalPrice = 0.0;
    private ReservationFX activeReservation = null;

    private boolean isDarkMode = true;
    private DropShadow logoEffect;
    private Scene mainScene;
    private StackPane centerStack;

    // --- NAVIGATION UI CONTROLS ---
    private Button btnNavHome, btnNavBook, btnNavDash, btnNavAdmin, btnNavAuth, btnThemeToggle;
    private Label lblUserStatus;

    // --- MEMORIZED PANELS FOR SPA ROUTING ---
    private ScrollPane welcomeView;
    private HBox authView;
    private BorderPane bookingView;
    private VBox paymentView;
    private VBox ticketView;
    private BorderPane dashboardView;
    private BorderPane adminView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Camrail Intercity - Réseau Ferroviaire National");
        
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");
        
        // 1. TOP NAVBAR
        HBox navbar = createNavbar();
        root.setTop(navbar);
        
        // 2. CENTER STACK FOR ROUTING
        centerStack = new StackPane();
        centerStack.setPadding(new Insets(20));
        root.setCenter(centerStack);

        // Initialize all main screens
        createWelcomeView();
        createAuthView();
        createBookingView();
        createPaymentView();
        createTicketView();
        createDashboardView();
        createAdminView();

        // 3. BOTTOM STATUS BAR
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(6, 20, 6, 20));
        statusBar.getStyleClass().add("status-bar-custom");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        
        Label statusLbl = new Label("SYSTEM STATUS: ONLINE // TRANSIT PROTOCOL ACTIVE // SECURE LINK ESTABLISHED");
        statusLbl.getStyleClass().add("status-lbl-custom");
        statusBar.getChildren().add(statusLbl);
        root.setBottom(statusBar);

        // Create main scene and load default CSS
        mainScene = new Scene(root, 1150, 820);
        applyTheme();
        
        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(750);
        primaryStage.show();

        // Default navigation
        navigateTo(welcomeView);
    }

    private void applyTheme() {
        mainScene.getStylesheets().clear();
        String themeFile = isDarkMode ? "stylefx.css" : "lightfx.css";
        String path = getClass().getResource(themeFile).toExternalForm();
        mainScene.getStylesheets().add(path);
        if (logoEffect != null) {
            logoEffect.setColor(isDarkMode ? Color.web("#00f0ff") : Color.web("#6d28d9"));
        }
    }

    // --- NAVIGATION MANAGER (ROUTING WITH TRANSITION) ---
    private void navigateTo(Node targetView) {
        if (centerStack.getChildren().isEmpty()) {
            centerStack.getChildren().add(targetView);
            return;
        }
        Node currentView = centerStack.getChildren().get(0);
        if (currentView == targetView) return;

        // Custom smooth fade transition
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), currentView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), targetView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeOut.setOnFinished(e -> {
            centerStack.getChildren().clear();
            centerStack.getChildren().add(targetView);
            targetView.setOpacity(0.0);
            fadeIn.play();
        });
        fadeOut.play();

        // Highlight active navigation button
        btnNavHome.setStyle("");
        btnNavBook.setStyle("");
        btnNavDash.setStyle("");
        btnNavAdmin.setStyle("");
        
        if (targetView == welcomeView) btnNavHome.setStyle("-fx-border-color: -primary-color; -fx-border-width: 0 0 2px 0;");
        else if (targetView == bookingView) btnNavBook.setStyle("-fx-border-color: -primary-color; -fx-border-width: 0 0 2px 0;");
        else if (targetView == dashboardView) btnNavDash.setStyle("-fx-border-color: -primary-color; -fx-border-width: 0 0 2px 0;");
        else if (targetView == adminView) btnNavAdmin.setStyle("-fx-border-color: -primary-color; -fx-border-width: 0 0 2px 0;");
    }

    // --- CREATE TOP NAVBAR ---
    private HBox createNavbar() {
        HBox nav = new HBox(20);
        nav.setPadding(new Insets(15, 30, 15, 30));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.getStyleClass().add("nav-bar-custom");
        
        // Brand logo
        Label logo = new Label("CAMRAIL");
        logo.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: -primary-color; -fx-letter-spacing: 2px;");
        logoEffect = new DropShadow();
        logoEffect.setColor(isDarkMode ? Color.web("#00f0ff") : Color.web("#6d28d9"));
        logoEffect.setRadius(8);
        logo.setEffect(logoEffect);
        
        Label brandSub = new Label("INTERCITY");
        brandSub.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: -secondary-color;");
        VBox logoBox = new VBox(-3, logo, brandSub);
        
        // Navigation Buttons
        btnNavHome = new Button("Accueil");
        btnNavBook = new Button("Réserver");
        btnNavDash = new Button("Mon Espace");
        btnNavAdmin = new Button("Administration");
        
        btnNavHome.getStyleClass().add("btn-secondary");
        btnNavBook.getStyleClass().add("btn-secondary");
        btnNavDash.getStyleClass().add("btn-secondary");
        btnNavAdmin.getStyleClass().add("btn-secondary");
        
        btnNavHome.setOnAction(e -> navigateTo(welcomeView));
        btnNavBook.setOnAction(e -> navigateTo(bookingView));
        btnNavDash.setOnAction(e -> {
            if (currentUser == null) {
                navigateTo(authView);
            } else {
                refreshDashboard();
                navigateTo(dashboardView);
            }
        });
        btnNavAdmin.setOnAction(e -> {
            if (currentUser != null && currentUser.isAdmin) {
                refreshAdmin();
                navigateTo(adminView);
            } else {
                showNotification("Accès restreint", "Vous devez être administrateur pour accéder à cette interface.", Alert.AlertType.ERROR);
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Session status label
        lblUserStatus = new Label("Visiteur");
        lblUserStatus.setStyle("-fx-text-fill: #8c91af; -fx-font-size: 12px; -fx-font-weight: bold;");

        // Auth action button
        btnNavAuth = new Button("Connexion");
        btnNavAuth.getStyleClass().add("btn-primary");
        btnNavAuth.setOnAction(e -> {
            if (currentUser == null) {
                navigateTo(authView);
            } else {
                handleLogout();
            }
        });

        // Theme Toggle button
        btnThemeToggle = new Button("🌓");
        btnThemeToggle.getStyleClass().add("btn-secondary");
        btnThemeToggle.setStyle("-fx-padding: 8px 12px;");
        btnThemeToggle.setOnAction(e -> {
            isDarkMode = !isDarkMode;
            applyTheme();
        });

        nav.getChildren().addAll(logoBox, btnNavHome, btnNavBook, btnNavDash, btnNavAdmin, spacer, lblUserStatus, btnNavAuth, btnThemeToggle);
        
        // Hide Admin tab by default
        btnNavAdmin.setVisible(false);
        btnNavDash.setVisible(false);

        return nav;
    }

    private void updateSessionUI() {
        if (currentUser != null) {
            lblUserStatus.setText(currentUser.prenom + " " + currentUser.name);
            btnNavAuth.setText("Déconnexion");
            btnNavAuth.getStyleClass().remove("btn-primary");
            if (!btnNavAuth.getStyleClass().contains("btn-logout")) {
                btnNavAuth.getStyleClass().add("btn-logout");
            }
            btnNavAuth.setStyle("");
            btnNavDash.setVisible(true);
            
            if (currentUser.isAdmin) {
                btnNavAdmin.setVisible(true);
            } else {
                btnNavAdmin.setVisible(false);
            }
        } else {
            lblUserStatus.setText("Visiteur");
            btnNavAuth.setText("Connexion");
            btnNavAuth.getStyleClass().remove("btn-logout");
            if (!btnNavAuth.getStyleClass().contains("btn-primary")) {
                btnNavAuth.getStyleClass().add("btn-primary");
            }
            btnNavAuth.setStyle(""); // Restore default CSS class style
            btnNavAdmin.setVisible(false);
            btnNavDash.setVisible(false);
        }
    }

    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Déconnexion");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment vous déconnecter de votre session ?");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            currentUser = null;
            updateSessionUI();
            navigateTo(welcomeView);
            showNotification("Déconnexion", "Vous avez été déconnecté avec succès.", Alert.AlertType.INFORMATION);
        }
    }

    // --- SCREEN 1: LANDING PAGE (WELCOMEVIEW) ---
    private void createWelcomeView() {
        VBox container = new VBox(30);
        container.setAlignment(Pos.TOP_CENTER);
        container.setPadding(new Insets(10, 0, 30, 0));

        // A. HERO SECTION
        StackPane heroPane = new StackPane();
        heroPane.setMinHeight(280);
        heroPane.setPrefHeight(300);
        heroPane.setStyle("-fx-background-color: linear-gradient(to bottom right, #080816, #18002a); -fx-background-radius: 18px;");
        
        ImageView heroImg = null;
        try {
            java.io.File file = new java.io.File("hero_train.jpg");
            if (file.exists()) {
                Image img = new Image(new java.io.FileInputStream(file));
                heroImg = new ImageView(img);
                heroImg.fitWidthProperty().bind(heroPane.widthProperty());
                heroImg.fitHeightProperty().bind(heroPane.heightProperty());
                heroImg.setPreserveRatio(false);
            }
        } catch (Exception ex) {
            System.err.println("Erreur chargement hero_train.jpg: " + ex.getMessage());
        }
        
        Rectangle paneClip = new Rectangle();
        paneClip.setArcWidth(36);
        paneClip.setArcHeight(36);
        paneClip.widthProperty().bind(heroPane.widthProperty());
        paneClip.heightProperty().bind(heroPane.heightProperty());
        heroPane.setClip(paneClip);

        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(8, 8, 22, 0.65);");
        
        VBox heroText = new VBox(15);
        heroText.setAlignment(Pos.CENTER);
        heroText.setPadding(new Insets(30));
        
        Label title = new Label("Voyagez partout en toute simplicité");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: #ffffff;");
        DropShadow ds = new DropShadow();
        ds.setRadius(10);
        ds.setColor(Color.web("#00f0ff"));
        title.setEffect(ds);
        
        Label subtitle = new Label("Réservez vos billets de train au Cameroun en quelques secondes au meilleur prix.");
        subtitle.setStyle("-fx-font-size: 15px; -fx-text-fill: #8c91af;");

        HBox heroButtons = new HBox(15);
        heroButtons.setAlignment(Pos.CENTER);
        Button btnBookNow = new Button("Réserver maintenant");
        btnBookNow.getStyleClass().add("btn-primary");
        btnBookNow.setOnAction(e -> navigateTo(bookingView));
        
        Button btnLearnMore = new Button("Se connecter");
        btnLearnMore.getStyleClass().add("btn-secondary");
        btnLearnMore.setOnAction(e -> {
            if (currentUser == null) navigateTo(authView);
            else navigateTo(dashboardView);
        });
        
        heroButtons.getChildren().addAll(btnBookNow, btnLearnMore);
        heroText.getChildren().addAll(title, subtitle, heroButtons);
        
        if (heroImg != null) {
            heroPane.getChildren().addAll(heroImg, overlay, heroText);
        } else {
            heroPane.getChildren().addAll(overlay, heroText);
        }

        // B. INTEGRATED SEARCH BAR
        GridPane searchBar = new GridPane();
        searchBar.getStyleClass().add("glass-panel-neon");
        searchBar.setPadding(new Insets(18, 25, 18, 25));
        searchBar.setHgap(15);
        searchBar.setVgap(10);
        
        TextField txtFrom = new TextField();
        txtFrom.setPromptText("Départ (ex: Yaoundé)");
        txtFrom.textProperty().bindBidirectional(fromCity);
        
        // Autocomplete context menus
        ContextMenu fromMenu = new ContextMenu();
        txtFrom.textProperty().addListener((obs, old, val) -> {
            if (val.trim().isEmpty()) {
                fromMenu.hide();
                return;
            }
            List<String> matches = DataStoreFX.cities.stream()
                .map(c -> c.name)
                .filter(name -> name.toLowerCase().contains(val.toLowerCase()))
                .collect(Collectors.toList());
            if (!matches.isEmpty()) {
                fromMenu.getItems().clear();
                for (String m : matches) {
                    MenuItem item = new MenuItem(m);
                    item.setOnAction(ev -> {
                        fromCity.set(m);
                        txtFrom.positionCaret(m.length());
                    });
                    fromMenu.getItems().add(item);
                }
                if (!fromMenu.isShowing()) {
                    fromMenu.show(txtFrom, javafx.geometry.Side.BOTTOM, 0, 0);
                }
            } else {
                fromMenu.hide();
            }
        });

        TextField txtTo = new TextField();
        txtTo.setPromptText("Destination (ex: Douala)");
        txtTo.textProperty().bindBidirectional(toCity);
        
        ContextMenu toMenu = new ContextMenu();
        txtTo.textProperty().addListener((obs, old, val) -> {
            if (val.trim().isEmpty()) {
                toMenu.hide();
                return;
            }
            List<String> matches = DataStoreFX.cities.stream()
                .map(c -> c.name)
                .filter(name -> name.toLowerCase().contains(val.toLowerCase()))
                .collect(Collectors.toList());
            if (!matches.isEmpty()) {
                toMenu.getItems().clear();
                for (String m : matches) {
                    MenuItem item = new MenuItem(m);
                    item.setOnAction(ev -> {
                        toCity.set(m);
                        txtTo.positionCaret(m.length());
                    });
                    toMenu.getItems().add(item);
                }
                if (!toMenu.isShowing()) {
                    toMenu.show(txtTo, javafx.geometry.Side.BOTTOM, 0, 0);
                }
            } else {
                toMenu.hide();
            }
        });

        DatePicker dpDate = new DatePicker(LocalDate.now().plusDays(1));
        dpDate.valueProperty().bindBidirectional(travelDate);
        dpDate.setStyle("-fx-pref-width: 170px;");

        Spinner<Integer> spPassengers = new Spinner<>(1, 10, 1);
        spPassengers.getValueFactory().valueProperty().addListener((obs, old, val) -> passengerCount.set(val));
        spPassengers.setStyle("-fx-pref-width: 100px;");

        ComboBox<String> cbClass = new ComboBox<>(FXCollections.observableArrayList("ECONOMIQUE", "BUSINESS", "PREMIERE"));
        cbClass.valueProperty().bindBidirectional(selectedClass);
        cbClass.setStyle("-fx-pref-width: 150px;");

        Button btnSearch = new Button("🔍 Rechercher");
        btnSearch.getStyleClass().add("btn-primary");
        btnSearch.setOnAction(e -> handleSearchFormSubmit());

        searchBar.add(new Label("Départ"), 0, 0);
        searchBar.add(txtFrom, 0, 1);
        searchBar.add(new Label("Destination"), 1, 0);
        searchBar.add(txtTo, 1, 1);
        searchBar.add(new Label("Date de voyage"), 2, 0);
        searchBar.add(dpDate, 2, 1);
        searchBar.add(new Label("Passagers"), 3, 0);
        searchBar.add(spPassengers, 3, 1);
        searchBar.add(new Label("Classe"), 4, 0);
        searchBar.add(cbClass, 4, 1);
        searchBar.add(btnSearch, 5, 1);

        for (int i = 0; i < 5; i++) {
            searchBar.getChildren().get(i * 2).setStyle("-fx-text-fill: -text-muted; -fx-font-weight: bold; -fx-font-size: 11px;");
        }

        // C. SECTION POPULAR DESTINATIONS
        VBox destSection = new VBox(15);
        Label lblDestTitle = new Label("Destinations Populaires");
        lblDestTitle.getStyleClass().add("title-medium");
        
        FlowPane destGrid = new FlowPane(20, 20);
        destGrid.setAlignment(Pos.CENTER);
        for (City c : DataStoreFX.cities) {
            VBox card = new VBox(10);
            card.getStyleClass().add("card-destination");
            card.setPadding(new Insets(15));
            card.setPrefWidth(200);
            card.setPrefHeight(150);
            
            Label name = new Label(c.name);
            name.getStyleClass().add("card-dest-name");
            
            Label desc = new Label(c.desc);
            desc.setWrapText(true);
            desc.getStyleClass().add("card-dest-desc");
            
            Label price = new Label("A partir de " + String.format("%,.0f", c.minPrice) + " F");
            price.getStyleClass().add("card-dest-price");
            
            Button btnQuickBook = new Button("Choisir");
            btnQuickBook.getStyleClass().add("btn-primary");
            btnQuickBook.setStyle("-fx-padding: 4px 10px; -fx-font-size: 10px;");
            btnQuickBook.setOnAction(e -> {
                fromCity.set("Yaoundé");
                toCity.set(c.name);
                navigateTo(bookingView);
                handleSearchFormSubmit();
            });

            card.getChildren().addAll(name, desc, price, btnQuickBook);
            destGrid.getChildren().add(card);
        }
        destSection.getChildren().addAll(lblDestTitle, destGrid);

        // D. SECTION PROMOTIONS & AVANTAGES
        VBox promoSection = new VBox(15);
        Label lblPromoTitle = new Label("Offres & Promotions de Saison");
        lblPromoTitle.getStyleClass().add("title-medium");

        FlowPane promoGrid = new FlowPane(20, 20);
        promoGrid.setAlignment(Pos.CENTER);
        for (Promo p : DataStoreFX.promos) {
            VBox card = new VBox(8);
            card.getStyleClass().add("glass-panel");
            card.setPadding(new Insets(15));
            card.setPrefWidth(260);

            Label tag = new Label(p.tag);
            tag.getStyleClass().add("promo-tag");
            
            Label titleP = new Label(p.title);
            titleP.getStyleClass().add("promo-title");
            
            Label descP = new Label(p.desc);
            descP.setWrapText(true);
            descP.getStyleClass().add("promo-desc");

            Label codeLabel = new Label("CODE : " + p.code);
            codeLabel.getStyleClass().add("promo-code");

            card.getChildren().addAll(tag, titleP, descP, codeLabel);
            promoGrid.getChildren().add(card);
        }
        promoSection.getChildren().addAll(lblPromoTitle, promoGrid);

        // E. REVIEWS CAROUSEL WITH AUTOMATIC ROTATION
        VBox reviewSection = new VBox(15);
        reviewSection.setAlignment(Pos.CENTER);
        Label lblReviewTitle = new Label("Ce que nos clients disent");
        lblReviewTitle.getStyleClass().add("title-medium");

        StackPane carouselContainer = new StackPane();
        carouselContainer.getStyleClass().add("glass-panel");
        carouselContainer.setPadding(new Insets(20));
        carouselContainer.setPrefHeight(120);
        carouselContainer.setMaxWidth(700);

        List<VBox> slides = new ArrayList<>();
        for (Review r : DataStoreFX.reviews) {
            VBox slide = new VBox(8);
            slide.setAlignment(Pos.CENTER);
            
            Label stars = new Label("★".repeat(r.rating) + "☆".repeat(5 - r.rating));
            stars.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 16px;");

            Label comment = new Label("\"" + r.text + "\"");
            comment.setWrapText(true);
            comment.getStyleClass().add("comment-text");
            
            Label author = new Label("- " + r.name);
            author.getStyleClass().add("comment-author");

            slide.getChildren().addAll(stars, comment, author);
            slides.add(slide);
        }

        // Add first slide
        carouselContainer.getChildren().add(slides.get(0));

        // Auto transition Timeline
        Timeline reviewTimeline = new Timeline(new KeyFrame(Duration.seconds(4), event -> {
            Node current = carouselContainer.getChildren().get(0);
            int idx = slides.indexOf((VBox) current);
            int nextIdx = (idx + 1) % slides.size();
            VBox next = slides.get(nextIdx);

            FadeTransition out = new FadeTransition(Duration.millis(300), current);
            out.setFromValue(1.0);
            out.setToValue(0.0);
            out.setOnFinished(e -> {
                carouselContainer.getChildren().clear();
                carouselContainer.getChildren().add(next);
                next.setOpacity(0.0);
                FadeTransition in = new FadeTransition(Duration.millis(300), next);
                in.setFromValue(0.0);
                in.setToValue(1.0);
                in.play();
            });
            out.play();
        }));
        reviewTimeline.setCycleCount(Animation.INDEFINITE);
        reviewTimeline.play();

        reviewSection.getChildren().addAll(lblReviewTitle, carouselContainer);

        // Put everything in the container
        container.getChildren().addAll(heroPane, searchBar, destSection, promoSection, reviewSection);

        welcomeView = new ScrollPane(container);
        welcomeView.setFitToWidth(true);
    }

    private void handleSearchFormSubmit() {
        if (fromCity.get().trim().isEmpty() || toCity.get().trim().isEmpty()) {
            showNotification("Erreur de recherche", "Veuillez spécifier une ville de départ et de destination.", Alert.AlertType.WARNING);
            return;
        }
        navigateTo(bookingView);
        executeSearch();
    }

    // Canvas component to draw static / dynamic neon high-speed train as hero illustration
    private static class CanvasTrain extends Region {
        public CanvasTrain() {
            setPrefSize(1100, 300);
            setMinSize(1100, 280);
            setMaxSize(1100, 300);
        }
        @Override
        protected void layoutChildren() {
            getChildren().clear();
            // We draw a neon cyber maglev line across the background using basic JavaFX shapes
            Line track = new Line(0, 240, 1100, 240);
            track.setStroke(Color.web("#00f0ff", 0.3));
            track.setStrokeWidth(4);
            
            Line track2 = new Line(0, 245, 1100, 245);
            track2.setStroke(Color.web("#00f0ff", 0.6));
            track2.setStrokeWidth(1);

            // Sleek aerodynamic train body
            Polygon body = new Polygon();
            body.getPoints().addAll(
                350.0, 235.0,
                750.0, 235.0,
                790.0, 220.0,
                730.0, 195.0,
                350.0, 195.0
            );
            body.setFill(Color.web("#121223"));
            body.setStroke(Color.web("#00f0ff"));
            body.setStrokeWidth(1.5);
            
            // Train window stripe
            Polygon window = new Polygon();
            window.getPoints().addAll(
                660.0, 210.0,
                720.0, 210.0,
                740.0, 220.0,
                660.0, 220.0
            );
            window.setFill(Color.web("#bd00ff", 0.8));

            getChildren().addAll(track, track2, body, window);
        }
    }

    // --- SCREEN 2: AUTHENTIFICATION (AUTHVIEW) ---
    private VBox authLoginCard, authRegisterCard;
    
    private void createAuthView() {
        authView = new HBox(0);
        authView.setAlignment(Pos.CENTER);
        authView.setStyle("-fx-background-color: -bg-dark;");

        // Left Side: Banner illustration
        VBox banner = new VBox(20);
        banner.setAlignment(Pos.CENTER);
        banner.setPadding(new Insets(40));
        banner.setPrefWidth(450);
        banner.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a0b1e, #bd00ff); -fx-background-radius: 16px 0 0 16px;");
        
        Label lblBannerTitle = new Label("Accédez à votre espace voyageur");
        lblBannerTitle.setWrapText(true);
        lblBannerTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        
        Label lblBannerText = new Label("Gérez vos e-billets, annulez en un clic et accumulez des points de fidélité.");
        lblBannerText.setWrapText(true);
        lblBannerText.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 13px;");
        banner.getChildren().addAll(lblBannerTitle, lblBannerText);

        // Right Side: Forms (Login / Register stacked)
        StackPane formsStack = new StackPane();
        formsStack.setStyle("-fx-background-radius: 0 16px 16px 0;");
        
        // 2A. LOGIN FORM
        authLoginCard = new VBox(15);
        authLoginCard.getStyleClass().add("glass-panel");
        authLoginCard.setPadding(new Insets(30, 40, 30, 40));
        authLoginCard.setPrefWidth(450);
        authLoginCard.setAlignment(Pos.CENTER_LEFT);
        
        Label lblLogTitle = new Label("Connexion");
        lblLogTitle.getStyleClass().add("title-large");
        
        TextField txtLogEmail = new TextField();
        txtLogEmail.setPromptText("Adresse email");
        
        PasswordField txtLogPass = new PasswordField();
        txtLogPass.setPromptText("Mot de passe");
        
        Button btnSubmitLogin = new Button("Se connecter");
        btnSubmitLogin.getStyleClass().add("btn-primary");
        btnSubmitLogin.setMaxWidth(Double.MAX_VALUE);
        
        Button btnToggleReg = new Button("Pas encore de compte ? S'inscrire");
        btnToggleReg.getStyleClass().add("btn-secondary");
        btnToggleReg.setMaxWidth(Double.MAX_VALUE);
        
        Button btnGoogleAuth = new Button("Continuer avec Google");
        btnGoogleAuth.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #1f2937; -fx-font-weight: bold;");
        btnGoogleAuth.setMaxWidth(Double.MAX_VALUE);
        btnGoogleAuth.setOnAction(e -> handleGoogleLogin());

        authLoginCard.getChildren().addAll(lblLogTitle, new Label("E-mail"), txtLogEmail, new Label("Mot de passe"), txtLogPass, btnSubmitLogin, btnGoogleAuth, btnToggleReg);
        
        // 2B. REGISTER FORM
        authRegisterCard = new VBox(12);
        authRegisterCard.getStyleClass().add("glass-panel");
        authRegisterCard.setPadding(new Insets(25, 40, 25, 40));
        authRegisterCard.setPrefWidth(450);
        authRegisterCard.setAlignment(Pos.CENTER_LEFT);
        authRegisterCard.setVisible(false);
        
        Label lblRegTitle = new Label("Inscription");
        lblRegTitle.getStyleClass().add("title-large");
        
        TextField txtRegName = new TextField();
        txtRegName.setPromptText("Nom");
        TextField txtRegPrenom = new TextField();
        txtRegPrenom.setPromptText("Prénom");
        TextField txtRegPhone = new TextField();
        txtRegPhone.setPromptText("Téléphone (+237)");
        TextField txtRegEmail = new TextField();
        txtRegEmail.setPromptText("E-mail");
        PasswordField txtRegPass = new PasswordField();
        txtRegPass.setPromptText("Mot de passe");
        PasswordField txtRegConfirm = new PasswordField();
        txtRegConfirm.setPromptText("Confirmer le mot de passe");
        
        Button btnSubmitReg = new Button("Créer un compte");
        btnSubmitReg.getStyleClass().add("btn-primary");
        btnSubmitReg.setMaxWidth(Double.MAX_VALUE);
        
        Button btnToggleLog = new Button("Déjà inscrit ? Se connecter");
        btnToggleLog.getStyleClass().add("btn-secondary");
        btnToggleLog.setMaxWidth(Double.MAX_VALUE);

        authRegisterCard.getChildren().addAll(lblRegTitle, txtRegName, txtRegPrenom, txtRegPhone, txtRegEmail, txtRegPass, txtRegConfirm, btnSubmitReg, btnToggleLog);

        formsStack.getChildren().addAll(authLoginCard, authRegisterCard);
        authView.getChildren().addAll(banner, formsStack);

        // Actions
        btnToggleReg.setOnAction(e -> switchAuthCard(false));
        btnToggleLog.setOnAction(e -> switchAuthCard(true));
        
        btnSubmitLogin.setOnAction(e -> {
            String email = txtLogEmail.getText().trim();
            String pass = txtLogPass.getText();
            Optional<User> match = DataStoreFX.users.stream()
                .filter(u -> u.email.equalsIgnoreCase(email) && u.password.equals(pass))
                .findFirst();
            if (match.isPresent()) {
                currentUser = match.get();
                updateSessionUI();
                txtLogEmail.clear();
                txtLogPass.clear();
                showNotification("Connexion réussie", "Bienvenue à bord, " + currentUser.prenom, Alert.AlertType.INFORMATION);
                
                // If in a booking tunnel, redirect to payment
                if (selectedSeatNumber != -1) {
                    preparePayment();
                    navigateTo(paymentView);
                } else {
                    refreshDashboard();
                    navigateTo(dashboardView);
                }
            } else {
                showNotification("Identifiants incorrects", "Veuillez vérifier votre email et mot de passe.", Alert.AlertType.ERROR);
            }
        });

        btnSubmitReg.setOnAction(e -> {
            String name = txtRegName.getText().trim();
            String prenom = txtRegPrenom.getText().trim();
            String phone = txtRegPhone.getText().trim();
            String email = txtRegEmail.getText().trim();
            String pass = txtRegPass.getText();
            String conf = txtRegConfirm.getText();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                showNotification("Champs requis", "Veuillez renseigner les champs obligatoires.", Alert.AlertType.WARNING);
                return;
            }
            if (!pass.equals(conf)) {
                showNotification("Mot de passe", "Les mots de passe ne correspondent pas.", Alert.AlertType.ERROR);
                return;
            }

            boolean exists = DataStoreFX.users.stream().anyMatch(u -> u.email.equalsIgnoreCase(email));
            if (exists) {
                showNotification("Email dupliqué", "Cette adresse email est déjà enregistrée.", Alert.AlertType.WARNING);
                return;
            }

            User nu = new User("usr-" + (100 + DataStoreFX.users.size()), name, prenom, phone, email, pass, false);
            DataStoreFX.users.add(nu);
            DataStoreFX.saveUsers();
            
            currentUser = nu;
            updateSessionUI();
            
            // Clean
            txtRegName.clear(); txtRegPrenom.clear(); txtRegPhone.clear(); txtRegEmail.clear(); txtRegPass.clear(); txtRegConfirm.clear();
            showNotification("Compte créé", "Votre compte a été enregistré avec succès.", Alert.AlertType.INFORMATION);
            
            if (selectedSeatNumber != -1) {
                preparePayment();
                navigateTo(paymentView);
            } else {
                refreshDashboard();
                navigateTo(dashboardView);
            }
        });
    }

    private void switchAuthCard(boolean toLogin) {
        VBox fromCard = toLogin ? authRegisterCard : authLoginCard;
        VBox toCard = toLogin ? authLoginCard : authRegisterCard;

        FadeTransition out = new FadeTransition(Duration.millis(200), fromCard);
        out.setFromValue(1.0);
        out.setToValue(0.0);
        out.setOnFinished(e -> {
            fromCard.setVisible(false);
            toCard.setVisible(true);
            toCard.setOpacity(0.0);
            FadeTransition in = new FadeTransition(Duration.millis(200), toCard);
            in.setFromValue(0.0);
            in.setToValue(1.0);
            in.play();
        });
        out.play();
    }

    private void handleGoogleLogin() {
        User googleUser = new User("usr-google", "Voyageur", "Google", "+237 688889900", "google.user@gmail.com", "google_mock", false);
        currentUser = googleUser;
        updateSessionUI();
        showNotification("Connexion Google", "Simulation de connexion avec Google réussie.", Alert.AlertType.INFORMATION);
        
        if (selectedSeatNumber != -1) {
            preparePayment();
            navigateTo(paymentView);
        } else {
            refreshDashboard();
            navigateTo(dashboardView);
        }
    }

    // --- SCREEN 3: SEARCH RESULTS & INTERACTIVE WAGON MAP (BOOKINGVIEW) ---
    private VBox resultsBox;
    private GridPane gridSeats;
    private Label lblSelectedSeatBadge, lblFinalPrice, lblWagonTitle;
    private ComboBox<String> classSelectDetail;
    private Button btnProceedPayment;
    private VBox timelineBox;
    private ScrollPane leftScroll;
    private VBox skeletonLoader;

    private void createBookingView() {
        bookingView = new BorderPane();
        
        // Left Column: Filter settings and train results list
        VBox leftColumn = new VBox(15);
        leftColumn.setPrefWidth(480);
        leftColumn.setPadding(new Insets(10));
        
        // Dynamic search input updates on change
        GridPane sideSearch = new GridPane();
        sideSearch.getStyleClass().add("glass-panel");
        sideSearch.setPadding(new Insets(12));
        sideSearch.setHgap(8);
        sideSearch.setVgap(8);

        TextField txtSideFrom = new TextField();
        txtSideFrom.setPromptText("De");
        txtSideFrom.textProperty().bindBidirectional(fromCity);

        TextField txtSideTo = new TextField();
        txtSideTo.setPromptText("À");
        txtSideTo.textProperty().bindBidirectional(toCity);

        DatePicker dpSideDate = new DatePicker();
        dpSideDate.valueProperty().bindBidirectional(travelDate);
        dpSideDate.setStyle("-fx-pref-width: 140px;");

        Button btnSideSearch = new Button("Rechercher");
        btnSideSearch.getStyleClass().add("btn-primary");
        btnSideSearch.setOnAction(e -> executeSearch());

        sideSearch.add(new Label("Départ"), 0, 0);
        sideSearch.add(txtSideFrom, 0, 1);
        sideSearch.add(new Label("Arrivée"), 1, 0);
        sideSearch.add(txtSideTo, 1, 1);
        sideSearch.add(new Label("Date"), 2, 0);
        sideSearch.add(dpSideDate, 2, 1);
        sideSearch.add(btnSideSearch, 3, 1);

        for (int i = 0; i < 3; i++) {
            sideSearch.getChildren().get(i * 2).setStyle("-fx-text-fill: -text-muted; -fx-font-size: 11px; -fx-font-weight: bold;");
        }

        // Sorting controls
        HBox sortBox = new HBox(10);
        sortBox.setAlignment(Pos.CENTER_LEFT);
        ComboBox<String> sortCombo = new ComboBox<>(FXCollections.observableArrayList("Heure", "Prix", "Durée"));
        sortCombo.setValue("Heure");
        sortCombo.setOnAction(e -> sortTrains(sortCombo.getValue()));
        sortBox.getChildren().addAll(new Label("Trier par :"), sortCombo);
        sortBox.getChildren().get(0).setStyle("-fx-text-fill: -text-muted; -fx-font-weight: bold;");

        // Skeleton Shimmer simulation for results loading
        skeletonLoader = new VBox(15);
        skeletonLoader.setPadding(new Insets(10));
        for (int i = 0; i < 3; i++) {
            VBox mockCard = new VBox(10);
            mockCard.getStyleClass().add("skeleton-card");
            
            Rectangle line1 = new Rectangle(120, 15);
            line1.setStyle("-fx-fill: -text-muted; -fx-opacity: 0.15;");
            Rectangle line2 = new Rectangle(280, 20);
            line2.setStyle("-fx-fill: -text-muted; -fx-opacity: 0.15;");
            Rectangle line3 = new Rectangle(100, 12);
            line3.setStyle("-fx-fill: -text-muted; -fx-opacity: 0.15;");
            
            mockCard.getChildren().addAll(line1, line2, line3);
            skeletonLoader.getChildren().add(mockCard);
        }
        skeletonLoader.setVisible(false);
        skeletonLoader.setManaged(false);

        // Train cards results box
        resultsBox = new VBox(15);
        leftScroll = new ScrollPane(resultsBox);
        leftScroll.setFitToWidth(true);
        leftScroll.setPrefHeight(450);

        leftColumn.getChildren().addAll(sideSearch, sortBox, skeletonLoader, leftScroll);
        bookingView.setLeft(leftColumn);

        // Right Column: Interactive Wagon Seating Map
        VBox rightColumn = new VBox(15);
        rightColumn.getStyleClass().add("glass-panel-neon");
        rightColumn.setPadding(new Insets(20));
        rightColumn.setPrefWidth(550);
        rightColumn.setAlignment(Pos.TOP_CENTER);
        
        lblWagonTitle = new Label("Cabine Voyageurs");
        lblWagonTitle.getStyleClass().add("title-medium");
        
        classSelectDetail = new ComboBox<>(FXCollections.observableArrayList("ECONOMIQUE", "BUSINESS", "PREMIERE"));
        classSelectDetail.valueProperty().bindBidirectional(selectedClass);
        classSelectDetail.setOnAction(e -> {
            updatePrice();
            generateSeatMap();
        });

        gridSeats = new GridPane();
        gridSeats.setHgap(10);
        gridSeats.setVgap(10);
        gridSeats.setAlignment(Pos.CENTER);

        // Legend
        HBox legend = new HBox(15);
        legend.setAlignment(Pos.CENTER);
        legend.getChildren().addAll(
            createLegendDot("🟢", "Libre"),
            createLegendDot("🔴", "Occupé"),
            createLegendDot("🟡", "Votre choix")
        );

        // Timeline of intermediate stops
        timelineBox = new VBox(8);
        timelineBox.setPadding(new Insets(10));
        timelineBox.getStyleClass().add("glass-panel");

        rightColumn.getChildren().addAll(lblWagonTitle, new Label("Choisissez votre classe :"), classSelectDetail, gridSeats, legend, new Label("Itinéraire & Halte"), timelineBox);
        
        bookingView.setCenter(rightColumn);

        // Bottom Bar: Selected details summary and CTA
        HBox summaryBar = new HBox(30);
        summaryBar.getStyleClass().add("glass-panel");
        summaryBar.setPadding(new Insets(15, 25, 15, 25));
        summaryBar.setAlignment(Pos.CENTER_LEFT);
        
        lblSelectedSeatBadge = new Label("Aucun siège sélectionné");
        lblSelectedSeatBadge.getStyleClass().add("selected-seat-badge");

        lblFinalPrice = new Label("Prix : - FCFA");
        lblFinalPrice.getStyleClass().add("final-price-label");

        Region smSpacer = new Region();
        HBox.setHgrow(smSpacer, Priority.ALWAYS);

        btnProceedPayment = new Button("Procéder au paiement ➔");
        btnProceedPayment.getStyleClass().add("btn-primary");
        btnProceedPayment.setDisable(true);
        btnProceedPayment.setOnAction(e -> {
            if (currentUser == null) {
                navigateTo(authView);
                showNotification("Connexion requise", "Veuillez vous connecter pour poursuivre la réservation.", Alert.AlertType.WARNING);
            } else {
                preparePayment();
                navigateTo(paymentView);
            }
        });

        summaryBar.getChildren().addAll(lblSelectedSeatBadge, lblFinalPrice, smSpacer, btnProceedPayment);
        bookingView.setBottom(summaryBar);

        // Load initially
        executeSearch();
    }

    private HBox createLegendDot(String emoji, String text) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER);
        Label dot = new Label(emoji);
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 11px; -fx-text-fill: #8c91af; -fx-font-weight: bold;");
        box.getChildren().addAll(dot, label);
        return box;
    }

    private void executeSearch() {
        skeletonLoader.setVisible(true);
        skeletonLoader.setManaged(true);
        leftScroll.setVisible(false);
        leftScroll.setManaged(false);

        // Shimmer simulation duration
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            skeletonLoader.setVisible(false);
            skeletonLoader.setManaged(false);
            leftScroll.setVisible(true);
            leftScroll.setManaged(true);
            renderSearchResults();
        }));
        timeline.play();
    }

    private String normalizeString(String s) {
        if (s == null) return "";
        return s.toLowerCase()
            .replace("é", "e")
            .replace("è", "e")
            .replace("ê", "e")
            .replace("ë", "e")
            .replace("à", "a")
            .replace("â", "a")
            .replace("î", "i")
            .replace("ï", "i")
            .replace("ô", "o")
            .replace("û", "u")
            .replace("ü", "u")
            .replace("ç", "c")
            .trim();
    }

    private void renderSearchResults() {
        resultsBox.getChildren().clear();
        String rawFrom = fromCity.get().trim();
        String rawTo = toCity.get().trim();
        String from = normalizeString(rawFrom);
        String to = normalizeString(rawTo);

        if (rawFrom.isEmpty() || rawTo.isEmpty()) {
            Label placeholder = new Label("Veuillez saisir les villes de départ et d'arrivée dans la barre de recherche.");
            placeholder.setStyle("-fx-text-fill: #8c91af; -fx-font-style: italic; -fx-alignment: center;");
            resultsBox.getChildren().add(placeholder);
            return;
        }

        List<Trajet> found = DataStoreFX.trains.stream()
            .filter(t -> normalizeString(t.getVilleDepart()).contains(from) && normalizeString(t.getVilleArrivee()).contains(to))
            .collect(Collectors.toList());

        if (found.isEmpty()) {
            String capFrom = rawFrom.length() > 0 ? rawFrom.substring(0, 1).toUpperCase() + rawFrom.substring(1) : "Départ";
            String capTo = rawTo.length() > 0 ? rawTo.substring(0, 1).toUpperCase() + rawTo.substring(1) : "Arrivée";
            
            Trajet t1 = new Trajet(capFrom, capTo, "08:15", 15000, 240);
            Trajet t2 = new Trajet(capFrom, capTo, "14:45", 25000, 240);
            
            DataStoreFX.trains.add(t1);
            DataStoreFX.trains.add(t2);
            
            found = new ArrayList<>();
            found.add(t1);
            found.add(t2);
        }

        for (Trajet t : found) {
            VBox card = new VBox(12);
            card.getStyleClass().add("glass-panel");
            card.setPadding(new Insets(15));
            card.setCursor(javafx.scene.Cursor.HAND);
            
            // Highlight when selected
            if (selectedTrain != null && selectedTrain.toString().equals(t.toString())) {
                card.getStyleClass().add("glass-panel-neon");
            }

            card.setOnMouseClicked(e -> {
                selectedTrain = t;
                selectedSeatNumber = -1;
                btnProceedPayment.setDisable(true);
                lblSelectedSeatBadge.setText("Aucun siège sélectionné");
                updatePrice();
                generateSeatMap();
                updateTimelineStops(t);
                renderSearchResults(); // refresh highlighting
            });

            HBox row1 = new HBox(10);
            row1.setAlignment(Pos.CENTER_LEFT);
            Label nomTrain = new Label(t.getNom());
            nomTrain.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: -text-light;");
            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);
            
            // Calculate final price base
            double priceBase = t.calculerPrixAvecClasse(ClasseVoyage.valueOf(selectedClass.get()));
            Label priceLbl = new Label(String.format("%,.0f", priceBase) + " F");
            priceLbl.setStyle("-fx-text-fill: -primary-color; -fx-font-size: 16px; -fx-font-weight: bold;");
            row1.getChildren().addAll(nomTrain, sp, priceLbl);

            HBox row2 = new HBox(15);
            row2.setAlignment(Pos.CENTER_LEFT);
            Label depTime = new Label(t.getHoraireDepartStr());
            depTime.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: -text-light;");
            
            Label arrow = new Label("➔");
            arrow.setStyle("-fx-text-fill: -secondary-color;");

            // Calculate arrival time
            int dur = t.getDureeMinutes();
            int h = dur / 60;
            int m = dur % 60;
            String durText = h + "h" + (m > 0 ? String.format("%02d", m) : "");
            
            Label arrTime = new Label(t.getHeureArrivee().format(DateTimeFormatter.ofPattern("HH:mm")));
            arrTime.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: -text-light;");

            Label duration = new Label("(" + durText + ")");
            duration.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 12px;");

            row2.getChildren().addAll(depTime, arrow, arrTime, duration);

            HBox row3 = new HBox(10);
            row3.setAlignment(Pos.CENTER_LEFT);
            Label remaining = new Label("30 places libres");
            remaining.setStyle("-fx-background-color: rgba(16,185,129,0.15); -fx-text-fill: #10b981; -fx-padding: 2px 6px; -fx-font-size: 10px; -fx-background-radius: 4px; -fx-font-weight: bold;");
            
            Label stations = new Label(t.getVilleDepart() + " à " + t.getVilleArrivee());
            stations.setStyle("-fx-text-fill: -text-muted; -fx-font-size: 11px;");

            row3.getChildren().addAll(remaining, stations);

            card.getChildren().addAll(row1, row2, row3);
            resultsBox.getChildren().add(card);
        }
    }

    private void sortTrains(String criteria) {
        if (criteria.equals("Prix")) {
            DataStoreFX.trains.sort((a, b) -> Double.compare(a.getPrixBase(), b.getPrixBase()));
        } else if (criteria.equals("Durée")) {
            DataStoreFX.trains.sort((a, b) -> Integer.compare(a.getDureeMinutes(), b.getDureeMinutes()));
        } else {
            DataStoreFX.trains.sort((a, b) -> a.getHoraireDepartStr().compareTo(b.getHoraireDepartStr()));
        }
        renderSearchResults();
    }

    private void updateTimelineStops(Trajet t) {
        timelineBox.getChildren().clear();
        
        String intermediate = "Gare d'Édéa";
        if (t.getVilleDepart().equals("Yaoundé") && t.getVilleArrivee().equals("Garoua")) {
            intermediate = "Gare de Ngaoundéré";
        }

        Label stop1 = new Label("🚉 Gare de Départ : " + t.getVilleDepart() + " (" + t.getHoraireDepartStr() + ")");
        Label stop2 = new Label("🛑 Halte : " + intermediate + " (Arrêt technique de 10 mins)");
        Label stop3 = new Label("🏁 Terminus : " + t.getVilleArrivee() + " (" + t.getHeureArrivee().format(DateTimeFormatter.ofPattern("HH:mm")) + ")");
        
        stop1.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 11px;");
        stop2.setStyle("-fx-text-fill: #bd00ff; -fx-font-size: 11px; -fx-font-weight: bold;");
        stop3.setStyle("-fx-text-fill: #00f0ff; -fx-font-size: 11px;");

        timelineBox.getChildren().addAll(stop1, stop2, stop3);
    }

    private void generateSeatMap() {
        gridSeats.getChildren().clear();
        if (selectedTrain == null) {
            Label placeholder = new Label("Sélectionnez un train à gauche pour afficher le wagon.");
            placeholder.setStyle("-fx-text-fill: #8c91af; -fx-font-style: italic;");
            gridSeats.add(placeholder, 0, 0);
            return;
        }

        // Fetch already reserved seats for this train and date
        List<Integer> occupied = DataStoreFX.reservations.stream()
            .filter(r -> r.trainCode.equals(selectedTrain.getNom()) && r.date.equals(travelDate.get().toString()) && !r.status.equals("Annulé"))
            .map(r -> r.siegeNum)
            .collect(Collectors.toList());

        String currentCls = selectedClass.get();
        boolean isVoitureA = currentCls.equals("PREMIERE") || currentCls.equals("BUSINESS");
        String voitureLabel = isVoitureA ? "Voiture A" : "Voiture B";
        lblWagonTitle.setText(voitureLabel + " (" + (isVoitureA ? "Première / Business" : "Économique") + ")");

        // Grid parameters: 5 rows, 5 columns (with center aisle at index 2)
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                if (col == 2) {
                    // Central Aisle spacer
                    Region spacer = new Region();
                    spacer.setPrefWidth(30);
                    gridSeats.add(spacer, col, row);
                    continue;
                }

                // Calculate local seat number in wagon (1 to 20)
                int colOffset = col < 2 ? col + 1 : col; // maps 0,1,3,4 to 1,2,3,4
                int vSeatNum = (row * 4) + colOffset;
                
                // Calculate database seat number
                int dbSeatNum = isVoitureA ? vSeatNum : (vSeatNum + 20);
                String seatLabel = (isVoitureA ? "A" : "B") + vSeatNum;

                Button seatBtn = new Button(seatLabel);
                seatBtn.getStyleClass().add("train-seat");
                
                // Check if seat matches the selected class
                boolean isClassCompatible = false;
                if (currentCls.equals("PREMIERE")) {
                    isClassCompatible = dbSeatNum <= 10;
                } else if (currentCls.equals("BUSINESS")) {
                    isClassCompatible = dbSeatNum > 10 && dbSeatNum <= 20;
                } else if (currentCls.equals("ECONOMIQUE")) {
                    isClassCompatible = dbSeatNum > 20 && dbSeatNum <= 40;
                }

                boolean isOccupied = occupied.contains(dbSeatNum);
                
                if (isOccupied) {
                    seatBtn.getStyleClass().add("occupied");
                    seatBtn.setDisable(true);
                } else if (!isClassCompatible) {
                    seatBtn.getStyleClass().add("disabled-class");
                    seatBtn.setDisable(true);
                } else if (selectedSeatNumber == dbSeatNum) {
                    seatBtn.getStyleClass().add("selected");
                } else {
                    seatBtn.getStyleClass().add("free");
                }

                seatBtn.setOnAction(e -> {
                    selectedSeatNumber = dbSeatNum;
                    lblSelectedSeatBadge.setText(voitureLabel + " - Siège " + seatLabel);
                    btnProceedPayment.setDisable(false);
                    updatePrice();
                    generateSeatMap(); // Refresh seat highlighting
                });

                gridSeats.add(seatBtn, col, row);
            }
        }
    }

    private void updatePrice() {
        if (selectedTrain == null) return;
        
        ClasseVoyage cv = ClasseVoyage.valueOf(selectedClass.get());
        double base = selectedTrain.calculerPrixAvecClasse(cv);
        finalPrice = base * passengerCount.get();

        lblFinalPrice.setText("Prix : " + String.format("%,.0f", finalPrice) + " FCFA");
    }

    // --- SCREEN 4: BILLING & PAYMENT GATEWAY (PAYMENTVIEW) ---
    private Label lblInvoiceRoute, lblInvoiceDate, lblInvoiceClass, lblInvoiceSeat, lblInvoiceTotal;
    private RadioButton radMomo, radCard, radPaypal;
    private VBox methodPanelMomo, methodPanelCard, methodPanelPaypal;
    private TextField txtCardName, txtCardNum, txtCardExp;
    private Label ccNamePreview, ccNumPreview, ccExpPreview;
    private ComboBox<String> cbOperator;
    private TextField txtMomoPhone;

    private void createPaymentView() {
        paymentView = new VBox(20);
        paymentView.setAlignment(Pos.TOP_CENTER);
        paymentView.setPadding(new Insets(10, 50, 30, 50));
        paymentView.setMaxWidth(800);

        Label title = new Label("Paiement Sécurisé");
        title.getStyleClass().add("title-large");

        HBox split = new HBox(30);
        split.setAlignment(Pos.TOP_CENTER);

        // Left Col: Invoice details
        VBox invoiceCard = new VBox(15);
        invoiceCard.getStyleClass().add("glass-panel-neon");
        invoiceCard.setPadding(new Insets(20));
        invoiceCard.setPrefWidth(350);

        Label invoiceTitle = new Label("Facture Électronique");
        invoiceTitle.getStyleClass().add("invoice-title-label");
        
        lblInvoiceRoute = new Label("Liaison : -");
        lblInvoiceDate = new Label("Date : -");
        lblInvoiceClass = new Label("Classe : -");
        lblInvoiceSeat = new Label("Siège : -");
        lblInvoiceTotal = new Label("Total TTC : -");
        lblInvoiceTotal.getStyleClass().add("invoice-total-label");

        // Tax breakdown (19.25% TVA)
        Label taxInfo = new Label("TVA incluse (19.25%)");
        taxInfo.getStyleClass().add("text-muted");

        invoiceCard.getChildren().addAll(invoiceTitle, new Separator(), lblInvoiceRoute, lblInvoiceDate, lblInvoiceClass, lblInvoiceSeat, new Separator(), lblInvoiceTotal, taxInfo);
        for (Node child : invoiceCard.getChildren()) {
            if (child instanceof Label && child != invoiceTitle && child != lblInvoiceTotal && child != taxInfo) {
                child.getStyleClass().add("invoice-detail-label");
            }
        }

        // Right Col: Payment methods
        VBox paymentSection = new VBox(15);
        paymentSection.getStyleClass().add("glass-panel");
        paymentSection.setPadding(new Insets(20));
        paymentSection.setPrefWidth(420);

        ToggleGroup payGroup = new ToggleGroup();
        radMomo = new RadioButton("Mobile Money (Orange/MTN)");
        radCard = new RadioButton("Carte Bancaire");
        radPaypal = new RadioButton("PayPal");
        
        radMomo.setToggleGroup(payGroup);
        radCard.setToggleGroup(payGroup);
        radPaypal.setToggleGroup(payGroup);
        radMomo.setSelected(true);

        // 1. Mobile Money Panel
        methodPanelMomo = new VBox(10);
        cbOperator = new ComboBox<>(FXCollections.observableArrayList("Orange Money", "MTN Mobile Money"));
        cbOperator.setValue("Orange Money");
        txtMomoPhone = new TextField();
        txtMomoPhone.setPromptText("Numéro de téléphone (+237 6...)");
        methodPanelMomo.getChildren().addAll(new Label("Opérateur"), cbOperator, new Label("Téléphone"), txtMomoPhone);

        // 2. Credit Card Panel with live interactive credit card preview representation
        methodPanelCard = new VBox(10);
        txtCardName = new TextField();
        txtCardName.setPromptText("Nom du titulaire");
        txtCardNum = new TextField();
        txtCardNum.setPromptText("•••• •••• •••• ••••");
        txtCardExp = new TextField();
        txtCardExp.setPromptText("MM/AA");
        
        // Live preview layout
        VBox ccCard = new VBox(15);
        ccCard.setStyle("-fx-background-color: linear-gradient(to bottom right, #1f1f3e, #bd00ff); -fx-background-radius: 12px; -fx-padding: 15px;");
        ccCard.setPrefHeight(120);
        
        Label ccChip = new Label("⚡ CHIP SYSTEM");
        ccChip.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #00f0ff; -fx-font-weight: bold;");
        ccNumPreview = new Label("•••• •••• •••• ••••");
        ccNumPreview.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 16px; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        
        HBox ccMeta = new HBox(20);
        ccNamePreview = new Label("NOM TITULAIRE");
        ccNamePreview.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 11px;");
        ccExpPreview = new Label("MM/AA");
        ccExpPreview.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 11px;");
        Region ccSpacer = new Region();
        HBox.setHgrow(ccSpacer, Priority.ALWAYS);
        ccMeta.getChildren().addAll(ccNamePreview, ccSpacer, ccExpPreview);

        ccCard.getChildren().addAll(ccChip, ccNumPreview, ccMeta);

        // Bind live updates
        txtCardName.textProperty().addListener((obs, old, val) -> ccNamePreview.setText(val.isEmpty() ? "NOM TITULAIRE" : val.toUpperCase()));
        txtCardNum.textProperty().addListener((obs, old, val) -> ccNumPreview.setText(val.isEmpty() ? "•••• •••• •••• ••••" : val));
        txtCardExp.textProperty().addListener((obs, old, val) -> ccExpPreview.setText(val.isEmpty() ? "MM/AA" : val));

        methodPanelCard.getChildren().addAll(ccCard, new Label("Nom sur la carte"), txtCardName, new Label("Numéro de carte"), txtCardNum, new Label("Date d'expiration"), txtCardExp);
        methodPanelCard.setVisible(false);
        methodPanelCard.setManaged(false);

        // 3. Paypal Panel
        methodPanelPaypal = new VBox(10);
        Label payPalMsg = new Label("Vous serez redirigé vers l'interface sécurisée de PayPal.");
        payPalMsg.setWrapText(true);
        payPalMsg.setStyle("-fx-text-fill: #8c91af; -fx-font-style: italic;");
        methodPanelPaypal.getChildren().addAll(payPalMsg);
        methodPanelPaypal.setVisible(false);
        methodPanelPaypal.setManaged(false);

        // Hide/Show correct panels on toggle
        payGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            methodPanelMomo.setVisible(false); methodPanelMomo.setManaged(false);
            methodPanelCard.setVisible(false); methodPanelCard.setManaged(false);
            methodPanelPaypal.setVisible(false); methodPanelPaypal.setManaged(false);

            if (newVal == radMomo) {
                methodPanelMomo.setVisible(true); methodPanelMomo.setManaged(true);
            } else if (newVal == radCard) {
                methodPanelCard.setVisible(true); methodPanelCard.setManaged(true);
            } else if (newVal == radPaypal) {
                methodPanelPaypal.setVisible(true); methodPanelPaypal.setManaged(true);
            }
        });

        Button btnPay = new Button("Valider et Payer");
        btnPay.getStyleClass().add("btn-primary");
        btnPay.setMaxWidth(Double.MAX_VALUE);
        btnPay.setOnAction(e -> handlePaymentConfirm(btnPay));

        paymentSection.getChildren().addAll(
            new Label("Sélectionnez votre moyen de paiement :"),
            radMomo, radCard, radPaypal,
            new Separator(),
            methodPanelMomo, methodPanelCard, methodPanelPaypal,
            new Separator(),
            btnPay
        );

        split.getChildren().addAll(invoiceCard, paymentSection);
        paymentView.getChildren().addAll(title, split);
    }

    private void preparePayment() {
        if (selectedTrain == null) return;
        lblInvoiceRoute.setText("Liaison : " + selectedTrain.getVilleDepart() + " ➔ " + selectedTrain.getVilleArrivee());
        lblInvoiceDate.setText("Date : " + travelDate.get().toString() + " (" + selectedTrain.getHoraireDepartStr() + ")");
        lblInvoiceClass.setText("Classe : " + selectedClass.get());
        
        boolean isVoitureA = selectedSeatNumber <= 20;
        String voiture = isVoitureA ? "Voiture A" : "Voiture B";
        String seatLabel = (isVoitureA ? "A" : "B") + (isVoitureA ? selectedSeatNumber : (selectedSeatNumber - 20));
        lblInvoiceSeat.setText("Siège : " + voiture + " - N°" + seatLabel);
        
        lblInvoiceTotal.setText("Total TTC : " + String.format("%,.0f", finalPrice) + " FCFA");
    }

    private void handlePaymentConfirm(Button payBtn) {
        String method = "Inconnu";
        if (radMomo.isSelected()) {
            String phone = txtMomoPhone.getText().trim();
            if (phone.isEmpty() || !phone.matches("^[6][0-9]{8}$")) {
                showNotification("Numéro invalide", "Veuillez entrer un numéro camerounais valide à 9 chiffres commençant par 6.", Alert.AlertType.WARNING);
                return;
            }
            method = cbOperator.getValue() + " (" + phone + ")";
        } else if (radCard.isSelected()) {
            String name = txtCardName.getText().trim();
            String num = txtCardNum.getText().trim();
            if (name.isEmpty() || num.isEmpty()) {
                showNotification("Données requises", "Veuillez renseigner les données de la carte de crédit.", Alert.AlertType.WARNING);
                return;
            }
            method = "Carte Bancaire (Visa/Mastercard)";
        } else if (radPaypal.isSelected()) {
            method = "PayPal Express Gateway";
        }

        // Simulate payment transition processing state
        payBtn.setDisable(true);
        payBtn.setText("Traitement sécurisé en cours...");

        String finalMethod = method;
        Timeline processing = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            payBtn.setDisable(false);
            payBtn.setText("Valider et Payer");
            
            // Create Swing reservation model object to generate ID and code
            ClasseVoyage cv = ClasseVoyage.ECONOMIQUE;
            if (selectedClass.get().equals("PREMIERE")) cv = ClasseVoyage.PREMIERE;
            else if (selectedClass.get().equals("BUSINESS")) cv = ClasseVoyage.BUSINESS;

            Siege s = new Siege(selectedSeatNumber, cv);
            Reservation swingReservation = new Reservation(selectedTrain, s, currentUser.name, currentUser.prenom);
            swingReservation.setTelephone(currentUser.phone);
            swingReservation.validerPaiement();

            // Save reservation to DataStoreFX
            String resId = swingReservation.getNumeroTicket();
            activeReservation = new ReservationFX(
                resId,
                currentUser.id,
                selectedTrain.getNom(),
                selectedTrain.getVilleDepart(),
                selectedTrain.getVilleArrivee(),
                travelDate.get().toString(),
                selectedTrain.getHoraireDepartStr(),
                selectedClass.get(),
                selectedSeatNumber,
                currentUser.name,
                currentUser.prenom,
                finalPrice,
                "Confirmé",
                finalMethod
            );
            
            DataStoreFX.reservations.add(activeReservation);
            DataStoreFX.saveReservations();

            // Clear selections
            selectedSeatNumber = -1;
            btnProceedPayment.setDisable(true);
            
            startPrintingSimulation(swingReservation);
        }));
        processing.play();
    }

    // --- SCREEN 5: ELECTRONIC TICKET DETAILS (TICKETVIEW) ---
    private Label lblTktCode, lblTktRoute, lblTktDate, lblTktTime, lblTktPassenger, lblTktClass, lblTktSeat, lblTktPrice, lblTktMethod;
    private VBox barcodePane, qrCodePane;

    private void createTicketView() {
        ticketView = new VBox(20);
        ticketView.setAlignment(Pos.TOP_CENTER);
        ticketView.setPadding(new Insets(10, 50, 30, 50));
        ticketView.setMaxWidth(700);

        Label title = new Label("Votre Billet Électronique");
        title.getStyleClass().add("title-large");

        // The ticket mockup representation (two parts, tear-off effect)
        VBox ticketCard = new VBox(15);
        ticketCard.getStyleClass().add("glass-panel-neon");
        ticketCard.setPadding(new Insets(25));
        ticketCard.setStyle("-fx-background-color: linear-gradient(to right, #0d0d1b, #191935); -fx-background-radius: 12px;");

        lblTktRoute = new Label("YAOUNDÉ ➔ DOUALA");
        lblTktRoute.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: #00f0ff;");

        GridPane tktGrid = new GridPane();
        tktGrid.setHgap(20);
        tktGrid.setVgap(12);

        lblTktCode = new Label("-");
        lblTktDate = new Label("-");
        lblTktTime = new Label("-");
        lblTktPassenger = new Label("-");
        lblTktClass = new Label("-");
        lblTktSeat = new Label("-");
        lblTktPrice = new Label("-");
        lblTktMethod = new Label("-");

        tktGrid.add(createTicketLabel("RÉSERVATION"), 0, 0);
        tktGrid.add(lblTktCode, 0, 1);
        tktGrid.add(createTicketLabel("DATE DE DEPART"), 1, 0);
        tktGrid.add(lblTktDate, 1, 1);
        tktGrid.add(createTicketLabel("HEURE"), 2, 0);
        tktGrid.add(lblTktTime, 2, 1);

        tktGrid.add(createTicketLabel("PASSAGER"), 0, 2);
        tktGrid.add(lblTktPassenger, 0, 3);
        tktGrid.add(createTicketLabel("CLASSE VOYAGE"), 1, 2);
        tktGrid.add(lblTktClass, 1, 3);
        tktGrid.add(createTicketLabel("SIÈGE"), 2, 2);
        tktGrid.add(lblTktSeat, 2, 3);

        tktGrid.add(createTicketLabel("TARIF TTC"), 0, 4);
        tktGrid.add(lblTktPrice, 0, 5);
        tktGrid.add(createTicketLabel("PAIEMENT"), 1, 4);
        tktGrid.add(lblTktMethod, 1, 5);

        // Stylize all content values
        for (Node n : tktGrid.getChildren()) {
            if (GridPane.getRowIndex(n) != null && GridPane.getRowIndex(n) % 2 != 0) {
                n.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13px;");
            }
        }
        lblTktPrice.setStyle("-fx-text-fill: #ff007f; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Tear-off separator
        HBox separator = new HBox(8);
        separator.setAlignment(Pos.CENTER);
        for (int i = 0; i < 35; i++) {
            Line dash = new Line(0, 0, 8, 0);
            dash.setStroke(Color.web("#8c91af", 0.4));
            separator.getChildren().add(dash);
        }

        // Custom Barcode & QR Code simulation drawing
        HBox codes = new HBox(40);
        codes.setAlignment(Pos.CENTER);

        barcodePane = new VBox(2);
        barcodePane.setAlignment(Pos.CENTER);
        generateBarcode();

        qrCodePane = new VBox(2);
        qrCodePane.setAlignment(Pos.CENTER);
        generateQRCode();

        codes.getChildren().addAll(barcodePane, qrCodePane);

        ticketCard.getChildren().addAll(lblTktRoute, new Separator(), tktGrid, separator, codes);

        HBox actions = new HBox(15);
        actions.setAlignment(Pos.CENTER);
        Button btnPrint = new Button("🖨️ Imprimer le Billet");
        btnPrint.getStyleClass().add("btn-primary");
        btnPrint.setOnAction(e -> simulatePrint());

        Button btnGoDashboard = new Button("OK");
        btnGoDashboard.getStyleClass().add("btn-secondary");
        btnGoDashboard.setOnAction(e -> {
            refreshDashboard();
            navigateTo(dashboardView);
        });

        actions.getChildren().addAll(btnPrint, btnGoDashboard);

        ticketView.getChildren().addAll(title, ticketCard, actions);
    }

    private Label createTicketLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #8c91af; -fx-font-size: 10px; -fx-font-weight: bold;");
        return l;
    }

    private void renderTicket() {
        if (activeReservation == null) return;
        lblTktCode.setText(activeReservation.id);
        lblTktRoute.setText(activeReservation.depart.toUpperCase() + " ➔ " + activeReservation.arrivee.toUpperCase());
        lblTktDate.setText(activeReservation.date);
        lblTktTime.setText(activeReservation.heure);
        lblTktPassenger.setText(activeReservation.prenomPassager + " " + activeReservation.nomPassager);
        lblTktClass.setText(activeReservation.classe);
        boolean isVoitureA = activeReservation.siegeNum <= 20;
        String voiture = isVoitureA ? "Voiture A" : "Voiture B";
        String seatLabel = (isVoitureA ? "A" : "B") + (isVoitureA ? activeReservation.siegeNum : (activeReservation.siegeNum - 20));
        lblTktSeat.setText(voiture + " - Siège " + seatLabel);
        lblTktPrice.setText(String.format("%,.0f", activeReservation.prixTotal) + " FCFA");
        lblTktMethod.setText(activeReservation.modePaiement);

        generateBarcode();
        generateQRCode();
    }

    private void generateBarcode() {
        barcodePane.getChildren().clear();
        Random r = new Random();
        HBox barcodeRow = new HBox(2);
        barcodeRow.setAlignment(Pos.CENTER);
        for (int i = 0; i < 30; i++) {
            Rectangle bar = new Rectangle(r.nextBoolean() ? 2 : 4, 40);
            bar.setFill(Color.web("#ffffff"));
            barcodeRow.getChildren().add(bar);
        }
        Label barcodeVal = new Label("*" + (activeReservation != null ? activeReservation.id : "BARCODE") + "*");
        barcodeVal.setStyle("-fx-font-family: 'Courier New'; -fx-text-fill: #8c91af; -fx-font-size: 10px;");
        barcodePane.getChildren().addAll(barcodeRow, barcodeVal);
    }

    private void generateQRCode() {
        qrCodePane.getChildren().clear();
        VBox qrBox = new VBox(2);
        qrBox.setAlignment(Pos.CENTER);
        for (int i = 0; i < 15; i++) {
            HBox qrRow = new HBox(2);
            for (int j = 0; j < 15; j++) {
                Rectangle cell = new Rectangle(3, 3);
                cell.setFill((i + j) % 2 == 0 || (i * j) % 5 == 0 ? Color.web("#ffffff") : Color.TRANSPARENT);
                qrRow.getChildren().add(cell);
            }
            qrBox.getChildren().add(qrRow);
        }
        qrCodePane.getChildren().add(qrBox);
    }

    private void simulatePrint() {
        if (activeReservation == null) return;
        Reservation r = recreateSwingReservation(activeReservation);
        startPrintingSimulation(r);
    }

    private Reservation recreateSwingReservation(ReservationFX fx) {
        Trajet train = DataStoreFX.trains.stream()
            .filter(t -> t.getNom().equals(fx.trainCode))
            .findFirst()
            .orElse(selectedTrain);
        
        ClasseVoyage cv = ClasseVoyage.ECONOMIQUE;
        if (fx.classe.equals("PREMIERE")) cv = ClasseVoyage.PREMIERE;
        else if (fx.classe.equals("BUSINESS")) cv = ClasseVoyage.BUSINESS;
        
        Siege s = new Siege(fx.siegeNum, cv);
        Reservation r = new Reservation(train, s, fx.nomPassager, fx.prenomPassager);
        r.setTelephone(currentUser.phone);
        try {
            java.lang.reflect.Field field = Reservation.class.getDeclaredField("numeroTicket");
            field.setAccessible(true);
            field.set(r, fx.id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        r.validerPaiement();
        return r;
    }

    private void startPrintingSimulation(Reservation reservation) {
        VBox overlay = new VBox(20);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(8, 8, 16, 0.85);");
        
        VBox glassPanel = new VBox(20);
        glassPanel.getStyleClass().add("glass-panel-neon");
        glassPanel.setPadding(new Insets(30));
        glassPanel.setMaxWidth(450);
        glassPanel.setAlignment(Pos.CENTER);
        
        Label lblIcon = new Label("🖨️");
        lblIcon.setStyle("-fx-font-size: 48px;");
        
        Label lblTitle = new Label("Impression du billet");
        lblTitle.getStyleClass().add("title-medium");
        
        Label lblStatus = new Label("Initialisation de l'impression...");
        lblStatus.setStyle("-fx-text-fill: #8c91af; -fx-font-size: 13px;");
        
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle("-fx-accent: #00f0ff;");
        
        glassPanel.getChildren().addAll(lblIcon, lblTitle, lblStatus, progressBar);
        overlay.getChildren().add(glassPanel);
        
        centerStack.getChildren().add(overlay);
        
        Timeline timeline = new Timeline();
        int steps = 25;
        for (int i = 1; i <= steps; i++) {
            final double progressVal = (double) i / steps;
            final int stepIndex = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * 100), e -> {
                progressBar.setProgress(progressVal);
                if (stepIndex < 7) {
                    lblStatus.setText("🖨️ Initialisation de l'impression...");
                } else if (stepIndex < 14) {
                    lblStatus.setText("🔐 Cryptage de la clé de transit...");
                } else if (stepIndex < 20) {
                    lblStatus.setText("💾 Écriture physique du ticket...");
                } else if (stepIndex < 25) {
                    lblStatus.setText("⚙️ Validation de la signature...");
                } else {
                    lblStatus.setText("✅ Terminé !");
                }
            });
            timeline.getKeyFrames().add(keyFrame);
        }
        
        timeline.setOnFinished(e -> {
            try {
                TicketWriter.genererTicket(reservation);
            } catch (Exception ex) {
                ex.printStackTrace();
                showNotification("Erreur d'impression", "Impossible d'écrire le billet physique: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
            
            centerStack.getChildren().remove(overlay);
            
            renderTicket();
            navigateTo(ticketView);
        });
        
        timeline.play();
    }

    private void resetAll() {
        fromCity.set("");
        toCity.set("");
        travelDate.set(LocalDate.now().plusDays(1));
        passengerCount.set(1);
        selectedClass.set("ECONOMIQUE");
        selectedTrain = null;
        selectedSeatNumber = -1;
        finalPrice = 0.0;
        activeReservation = null;
        navigateTo(welcomeView);
    }

    // --- SCREEN 6: USER SPACE DASHBOARD (DASHBOARDVIEW) ---
    private TableView<ReservationFX> tblUserBookings;
    private TextField txtProfName, txtProfPrenom, txtProfPhone, txtProfEmail;
    private PasswordField txtProfPass;

    private void createDashboardView() {
        dashboardView = new BorderPane();

        // Left Navigation Tab selector
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.getStyleClass().add("dashboard-sidebar");
        
        Label menuTitle = new Label("Tableau de bord");
        menuTitle.setStyle("-fx-text-fill: -text-light; -fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnTabTickets = new Button("🎫 Mes Billets");
        Button btnTabProfile = new Button("👤 Modifier Profil");
        
        btnTabTickets.getStyleClass().add("btn-secondary");
        btnTabProfile.getStyleClass().add("btn-secondary");
        btnTabTickets.setStyle("-fx-border-color: -primary-color; -fx-border-width: 0 0 0 4px; -fx-background-color: rgba(124, 58, 237, 0.08);");
        btnTabTickets.setMaxWidth(Double.MAX_VALUE);
        btnTabProfile.setMaxWidth(Double.MAX_VALUE);

        sidebar.getChildren().addAll(menuTitle, new Separator(), btnTabTickets, btnTabProfile);
        dashboardView.setLeft(sidebar);

        // Center Views (TabPane replacement for dynamic rendering)
        StackPane dbStack = new StackPane();
        dbStack.setPadding(new Insets(10, 20, 10, 20));
        dashboardView.setCenter(dbStack);

        // Subview 1: Tickets Table
        VBox subTickets = new VBox(15);
        Label lblTktTitle = new Label("Historique de vos réservations");
        lblTktTitle.getStyleClass().add("title-medium");

        tblUserBookings = new TableView<>();
        
        TableColumn<ReservationFX, String> colId = new TableColumn<>("Référence");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(90);

        TableColumn<ReservationFX, String> colRoute = new TableColumn<>("Trajet");
        colRoute.setCellValueFactory(cellData -> {
            ReservationFX r = cellData.getValue();
            return new SimpleStringProperty(r.depart + " ➔ " + r.arrivee);
        });
        colRoute.setPrefWidth(180);

        TableColumn<ReservationFX, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cellData -> {
            ReservationFX r = cellData.getValue();
            return new SimpleStringProperty(r.date + " à " + r.heure);
        });
        colDate.setPrefWidth(150);

        TableColumn<ReservationFX, Integer> colSeat = new TableColumn<>("Siège");
        colSeat.setCellValueFactory(new PropertyValueFactory<>("siegeNum"));
        colSeat.setPrefWidth(60);

        TableColumn<ReservationFX, Double> colPrice = new TableColumn<>("Tarif");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("prixTotal"));
        colPrice.setPrefWidth(90);

        TableColumn<ReservationFX, String> colStatus = new TableColumn<>("Statut");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(90);

        tblUserBookings.getColumns().addAll(colId, colRoute, colDate, colSeat, colPrice, colStatus);
        
        HBox tktActions = new HBox(15);
        Button btnViewTkt = new Button("Consulter le Billet");
        btnViewTkt.getStyleClass().add("btn-primary");
        btnViewTkt.setOnAction(e -> {
            ReservationFX selected = tblUserBookings.getSelectionModel().getSelectedItem();
            if (selected != null) {
                activeReservation = selected;
                renderTicket();
                navigateTo(ticketView);
            }
        });

        Button btnCancelTkt = new Button("Annuler Réservation");
        btnCancelTkt.getStyleClass().add("btn-neon-pink");
        btnCancelTkt.setOnAction(e -> {
            ReservationFX selected = tblUserBookings.getSelectionModel().getSelectedItem();
            if (selected != null) {
                if (selected.status.equals("Annulé")) {
                    showNotification("Déjà annulé", "Cette réservation est déjà annulée.", Alert.AlertType.WARNING);
                    return;
                }
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Annulation de voyage");
                confirm.setHeaderText(null);
                confirm.setContentText("Êtes-vous sûr de vouloir annuler ce voyage ? Un remboursement partiel sera effectué.");
                Optional<ButtonType> opt = confirm.showAndWait();
                if (opt.isPresent() && opt.get() == ButtonType.OK) {
                    selected.status = "Annulé";
                    DataStoreFX.saveReservations();
                    refreshDashboard();
                    showNotification("Annulé", "Le voyage a été annulé avec succès.", Alert.AlertType.INFORMATION);
                }
            }
        });

        tktActions.getChildren().addAll(btnViewTkt, btnCancelTkt);
        subTickets.getChildren().addAll(lblTktTitle, tblUserBookings, tktActions);

        // Subview 2: Profile modification Editor
        VBox subProfile = new VBox(12);
        subProfile.setMaxWidth(400);
        subProfile.setAlignment(Pos.CENTER_LEFT);
        Label lblProfTitle = new Label("Modifier votre profil");
        lblProfTitle.getStyleClass().add("title-medium");

        txtProfName = new TextField();
        txtProfPrenom = new TextField();
        txtProfPhone = new TextField();
        txtProfEmail = new TextField();
        txtProfPass = new PasswordField();
        txtProfPass.setPromptText("Saisissez un nouveau mot de passe pour changer");

        Button btnSaveProfile = new Button("Enregistrer les modifications");
        btnSaveProfile.getStyleClass().add("btn-primary");
        btnSaveProfile.setOnAction(e -> handleProfileUpdate());

        subProfile.getChildren().addAll(
            lblProfTitle,
            new Label("Nom"), txtProfName,
            new Label("Prénom"), txtProfPrenom,
            new Label("Téléphone"), txtProfPhone,
            new Label("E-mail"), txtProfEmail,
            new Label("Nouveau mot de passe"), txtProfPass,
            btnSaveProfile
        );
        subProfile.setVisible(false);

        dbStack.getChildren().addAll(subTickets, subProfile);

        // Toggle subviews
        btnTabProfile.setOnAction(e -> {
            subTickets.setVisible(false);
            subProfile.setVisible(true);
            btnTabProfile.setStyle("-fx-border-color: -primary-color; -fx-border-width: 0 0 0 4px; -fx-background-color: rgba(124, 58, 237, 0.08);");
            btnTabTickets.setStyle("");
        });
        btnTabTickets.setOnAction(e -> {
            subTickets.setVisible(true);
            subProfile.setVisible(false);
            btnTabTickets.setStyle("-fx-border-color: -primary-color; -fx-border-width: 0 0 0 4px; -fx-background-color: rgba(124, 58, 237, 0.08);");
            btnTabProfile.setStyle("");
        });
    }

    private void refreshDashboard() {
        if (currentUser == null) return;
        
        // Refresh User Profil form fields
        txtProfName.setText(currentUser.name);
        txtProfPrenom.setText(currentUser.prenom);
        txtProfPhone.setText(currentUser.phone);
        txtProfEmail.setText(currentUser.email);
        txtProfPass.clear();

        // Filter reservations matching user
        List<ReservationFX> matched = DataStoreFX.reservations.stream()
            .filter(r -> r.userId.equals(currentUser.id))
            .collect(Collectors.toList());
        tblUserBookings.setItems(FXCollections.observableArrayList(matched));
    }

    private void handleProfileUpdate() {
        if (currentUser == null) return;
        currentUser.name = txtProfName.getText().trim();
        currentUser.prenom = txtProfPrenom.getText().trim();
        currentUser.phone = txtProfPhone.getText().trim();
        currentUser.email = txtProfEmail.getText().trim();
        
        if (!txtProfPass.getText().isEmpty()) {
            currentUser.password = txtProfPass.getText();
        }

        DataStoreFX.saveUsers();
        updateSessionUI();
        showNotification("Profil mis à jour", "Vos modifications ont été enregistrées avec succès.", Alert.AlertType.INFORMATION);
    }

    // --- SCREEN 7: ADMIN DASHBOARD (ADMINVIEW) ---
    private Label adminRevenueLbl, adminBookingsLbl, adminUsersLbl, adminOccupLbl;
    private TableView<Trajet> tblAdminTrains;
    private TableView<ReservationFX> tblAdminBookings;
    private AreaChart<String, Number> salesChart;
    private PieChart classChart;

    private void createAdminView() {
        adminView = new BorderPane();

        // Left Navigation Sidebar
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: rgba(18, 18, 35, 0.4); -fx-border-color: transparent rgba(255, 255, 255, 0.05) transparent transparent; -fx-border-width: 0 1px 0 0;");

        Label menuTitle = new Label("Administration");
        menuTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnAdminStats = new Button("📊 Statistiques");
        Button btnAdminTrains = new Button("🚄 Gestion Trains");
        Button btnAdminReservs = new Button("🎫 Réservations");

        btnAdminStats.getStyleClass().add("btn-secondary");
        btnAdminTrains.getStyleClass().add("btn-secondary");
        btnAdminReservs.getStyleClass().add("btn-secondary");
        
        btnAdminStats.setMaxWidth(Double.MAX_VALUE);
        btnAdminTrains.setMaxWidth(Double.MAX_VALUE);
        btnAdminReservs.setMaxWidth(Double.MAX_VALUE);

        sidebar.getChildren().addAll(menuTitle, new Separator(), btnAdminStats, btnAdminTrains, btnAdminReservs);
        adminView.setLeft(sidebar);

        StackPane adminStack = new StackPane();
        adminStack.setPadding(new Insets(10, 20, 10, 20));
        adminView.setCenter(adminStack);

        // SUBVIEW 1: STATISTICS (WITH CHARTS)
        VBox subStats = new VBox(20);
        
        // Counter Cards
        HBox counters = new HBox(15);
        counters.setAlignment(Pos.CENTER);
        
        VBox cardRev = createCounterCard("CHIFFRE D'AFFAIRES", adminRevenueLbl = new Label("0 F"));
        VBox cardBook = createCounterCard("RESERVATIONS", adminBookingsLbl = new Label("0"));
        VBox cardUsers = createCounterCard("UTILISATEURS", adminUsersLbl = new Label("0"));
        VBox cardOccup = createCounterCard("TAUX OCCUPATION", adminOccupLbl = new Label("0%"));
        
        counters.getChildren().addAll(cardRev, cardBook, cardUsers, cardOccup);

        // JavaFX Charts for dynamic visuals
        HBox chartsBox = new HBox(20);
        chartsBox.setAlignment(Pos.CENTER);

        // Sales Trend Line chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Mois");
        salesChart = new AreaChart<>(xAxis, yAxis);
        salesChart.setTitle("Chiffre d'Affaires Mensuel");
        salesChart.setPrefSize(400, 260);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventes (FCFA)");
        series.getData().add(new XYChart.Data<>("Avril", 1200000));
        series.getData().add(new XYChart.Data<>("Mai", 3000000));
        series.getData().add(new XYChart.Data<>("Juin", 2300000));
        series.getData().add(new XYChart.Data<>("Juillet", 4800000));
        salesChart.getData().add(series);

        // Class Distribution Pie Chart
        classChart = new PieChart();
        classChart.setTitle("Répartition par Classe");
        classChart.setPrefSize(350, 260);

        chartsBox.getChildren().addAll(salesChart, classChart);
        subStats.getChildren().addAll(counters, chartsBox);

        // SUBVIEW 2: CRUD TRAINS
        VBox subTrains = new VBox(15);
        Label lblTrainTitle = new Label("Gestion des liaisons ferroviaires");
        lblTrainTitle.getStyleClass().add("title-medium");

        tblAdminTrains = new TableView<>();
        
        TableColumn<Trajet, String> tColId = new TableColumn<>("ID");
        tColId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom().substring(0, 3)));
        tColId.setPrefWidth(60);

        TableColumn<Trajet, String> tColRoute = new TableColumn<>("Liaison");
        tColRoute.setCellValueFactory(cellData -> {
            Trajet t = cellData.getValue();
            return new SimpleStringProperty(t.getVilleDepart() + " ➔ " + t.getVilleArrivee());
        });
        tColRoute.setPrefWidth(220);

        TableColumn<Trajet, String> tColTime = new TableColumn<>("Horaire");
        tColTime.setCellValueFactory(new PropertyValueFactory<>("horaireDepartStr"));
        tColTime.setPrefWidth(90);

        TableColumn<Trajet, Double> tColPrice = new TableColumn<>("Prix Base");
        tColPrice.setCellValueFactory(new PropertyValueFactory<>("prixBase"));
        tColPrice.setPrefWidth(100);

        TableColumn<Trajet, Integer> tColDuration = new TableColumn<>("Durée (mins)");
        tColDuration.setCellValueFactory(new PropertyValueFactory<>("dureeMinutes"));
        tColDuration.setPrefWidth(90);

        tblAdminTrains.getColumns().addAll(tColId, tColRoute, tColTime, tColPrice, tColDuration);

        HBox trainActions = new HBox(15);
        Button btnAddTrain = new Button("+ Ajouter un Train");
        btnAddTrain.getStyleClass().add("btn-primary");
        btnAddTrain.setOnAction(e -> showAddTrainDialog());

        Button btnDeleteTrain = new Button("Supprimer Liaison");
        btnDeleteTrain.getStyleClass().add("btn-neon-pink");
        btnDeleteTrain.setOnAction(e -> {
            Trajet selected = tblAdminTrains.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Suppression de Train");
                confirm.setHeaderText(null);
                confirm.setContentText("Voulez-vous vraiment supprimer cette liaison ?");
                Optional<ButtonType> opt = confirm.showAndWait();
                if (opt.isPresent() && opt.get() == ButtonType.OK) {
                    DataStoreFX.trains.remove(selected);
                    refreshAdmin();
                    showNotification("Supprimé", "Le train a été retiré avec succès.", Alert.AlertType.INFORMATION);
                }
            }
        });
        trainActions.getChildren().addAll(btnAddTrain, btnDeleteTrain);
        subTrains.getChildren().addAll(lblTrainTitle, tblAdminTrains, trainActions);
        subTrains.setVisible(false);

        // SUBVIEW 3: RESERVATIONS LIST
        VBox subReservations = new VBox(15);
        Label lblResTitle = new Label("Liste globale des réservations passagers");
        lblResTitle.getStyleClass().add("title-medium");

        tblAdminBookings = new TableView<>();
        
        TableColumn<ReservationFX, String> bColId = new TableColumn<>("Ref");
        bColId.setCellValueFactory(new PropertyValueFactory<>("id"));
        bColId.setPrefWidth(80);

        TableColumn<ReservationFX, String> bColPassenger = new TableColumn<>("Passager");
        bColPassenger.setCellValueFactory(cellData -> {
            ReservationFX r = cellData.getValue();
            return new SimpleStringProperty(r.prenomPassager + " " + r.nomPassager);
        });
        bColPassenger.setPrefWidth(160);

        TableColumn<ReservationFX, String> bColRoute = new TableColumn<>("Trajet");
        bColRoute.setCellValueFactory(cellData -> {
            ReservationFX r = cellData.getValue();
            return new SimpleStringProperty(r.depart + " ➔ " + r.arrivee);
        });
        bColRoute.setPrefWidth(180);

        TableColumn<ReservationFX, String> bColDate = new TableColumn<>("Date");
        bColDate.setCellValueFactory(cellData -> {
            ReservationFX r = cellData.getValue();
            return new SimpleStringProperty(r.date + " à " + r.heure);
        });
        bColDate.setPrefWidth(150);

        TableColumn<ReservationFX, String> bColClass = new TableColumn<>("Classe");
        bColClass.setCellValueFactory(new PropertyValueFactory<>("classe"));
        bColClass.setPrefWidth(90);

        TableColumn<ReservationFX, String> bColStatus = new TableColumn<>("Statut");
        bColStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        bColStatus.setPrefWidth(80);

        tblAdminBookings.getColumns().addAll(bColId, bColPassenger, bColRoute, bColDate, bColClass, bColStatus);
        
        subReservations.getChildren().addAll(lblResTitle, tblAdminBookings);
        subReservations.setVisible(false);

        adminStack.getChildren().addAll(subStats, subTrains, subReservations);

        // Sidebar actions
        btnAdminStats.setOnAction(e -> {
            subStats.setVisible(true); subTrains.setVisible(false); subReservations.setVisible(false);
            btnAdminStats.setStyle("-fx-border-color: #00f0ff; -fx-border-width: 0 0 0 3px;");
            btnAdminTrains.setStyle(""); btnAdminReservs.setStyle("");
        });
        btnAdminTrains.setOnAction(e -> {
            subStats.setVisible(false); subTrains.setVisible(true); subReservations.setVisible(false);
            btnAdminTrains.setStyle("-fx-border-color: #00f0ff; -fx-border-width: 0 0 0 3px;");
            btnAdminStats.setStyle(""); btnAdminReservs.setStyle("");
        });
        btnAdminReservs.setOnAction(e -> {
            subStats.setVisible(false); subTrains.setVisible(false); subReservations.setVisible(true);
            btnAdminReservs.setStyle("-fx-border-color: #00f0ff; -fx-border-width: 0 0 0 3px;");
            btnAdminStats.setStyle(""); btnAdminTrains.setStyle("");
        });
    }

    private VBox createCounterCard(String title, Label valLabel) {
        VBox card = new VBox(6);
        card.getStyleClass().add("glass-panel");
        card.setPadding(new Insets(12, 20, 12, 20));
        card.setPrefWidth(170);
        
        Label tLabel = new Label(title);
        tLabel.setStyle("-fx-text-fill: #8c91af; -fx-font-size: 9px; -fx-font-weight: bold;");
        
        valLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00f0ff;");
        card.getChildren().addAll(tLabel, valLabel);
        return card;
    }

    private void refreshAdmin() {
        // Calculate Statistics
        double revenue = DataStoreFX.reservations.stream()
            .filter(r -> r.status.equals("Confirmé"))
            .mapToDouble(r -> r.prixTotal)
            .sum();
        adminRevenueLbl.setText(String.format("%,.0f", revenue) + " F");

        long confirmedCount = DataStoreFX.reservations.stream()
            .filter(r -> r.status.equals("Confirmé"))
            .count();
        adminBookingsLbl.setText(String.valueOf(confirmedCount));

        long clientUsers = DataStoreFX.users.stream().filter(u -> !u.isAdmin).count();
        adminUsersLbl.setText(String.valueOf(clientUsers));

        // Occupation rate
        int totalSeatsReserved = DataStoreFX.reservations.stream().filter(r -> r.status.equals("Confirmé")).collect(Collectors.toList()).size();
        int totalCapacity = DataStoreFX.trains.size() * 30; // 30 seats per train
        int occup = totalCapacity > 0 ? (totalSeatsReserved * 100 / totalCapacity) : 0;
        adminOccupLbl.setText(occup + "%");

        // Load Tables
        tblAdminTrains.setItems(FXCollections.observableArrayList(DataStoreFX.trains));
        tblAdminBookings.setItems(FXCollections.observableArrayList(DataStoreFX.reservations));

        // Update Pie Chart with class stats
        long eco = DataStoreFX.reservations.stream().filter(r -> r.classe.equals("ECONOMIQUE") && r.status.equals("Confirmé")).count();
        long bus = DataStoreFX.reservations.stream().filter(r -> r.classe.equals("BUSINESS") && r.status.equals("Confirmé")).count();
        long prem = DataStoreFX.reservations.stream().filter(r -> r.classe.equals("PREMIERE") && r.status.equals("Confirmé")).count();

        // Fallbacks if no data to display nicely
        if (eco == 0 && bus == 0 && prem == 0) { eco = 10; bus = 5; prem = 2; }

        classChart.getData().clear();
        classChart.getData().addAll(
            new PieChart.Data("Eco (" + eco + ")", eco),
            new PieChart.Data("Business (" + bus + ")", bus),
            new PieChart.Data("Première (" + prem + ")", prem)
        );
    }

    private void showAddTrainDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Ajouter une nouvelle liaison ferroviaire");

        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #0c0c1b;");
        form.getStylesheets().add(mainScene.getStylesheets().get(0));

        TextField txtName = new TextField();
        txtName.setPromptText("Nom du train (ex: Intercity Express)");
        
        TextField txtFrom = new TextField();
        txtFrom.setPromptText("Ville de départ");
        
        TextField txtTo = new TextField();
        txtTo.setPromptText("Ville d'arrivée");
        
        TextField txtTime = new TextField();
        txtTime.setPromptText("Horaire (HH:mm)");
        
        TextField txtDuration = new TextField();
        txtDuration.setPromptText("Durée en minutes (ex: 240)");
        
        TextField txtPrice = new TextField();
        txtPrice.setPromptText("Prix de base (FCFA)");

        Button btnSave = new Button("Enregistrer");
        btnSave.getStyleClass().add("btn-primary");
        btnSave.setMaxWidth(Double.MAX_VALUE);
        btnSave.setOnAction(e -> {
            try {
                String name = txtName.getText().trim();
                String from = txtFrom.getText().trim();
                String to = txtTo.getText().trim();
                String time = txtTime.getText().trim();
                int dur = Integer.parseInt(txtDuration.getText());
                double price = Double.parseDouble(txtPrice.getText());

                if (name.isEmpty() || from.isEmpty() || to.isEmpty() || time.isEmpty()) {
                    showNotification("Champs requis", "Tous les champs doivent être saisis.", Alert.AlertType.WARNING);
                    return;
                }

                Trajet nt = new Trajet(from, to, time, price, dur);
                // We mock the Product Name to represent Custom Name
                nt.setNom(name);

                DataStoreFX.trains.add(nt);
                refreshAdmin();
                dialog.close();
                showNotification("Succès", "Le train " + name + " a été ajouté.", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException ex) {
                showNotification("Format incorrect", "Veuillez entrer des valeurs numériques pour la durée et le prix.", Alert.AlertType.ERROR);
            }
        });

        form.getChildren().addAll(
            new Label("Nom du train"), txtName,
            new Label("Gare Départ"), txtFrom,
            new Label("Gare Arrivée"), txtTo,
            new Label("Horaire de départ"), txtTime,
            new Label("Durée de voyage (min)"), txtDuration,
            new Label("Tarif de base (FCFA)"), txtPrice,
            new Separator(),
            btnSave
        );

        for (Node n : form.getChildren()) {
            if (n instanceof Label) n.setStyle("-fx-text-fill: #8c91af; -fx-font-weight: bold;");
        }

        Scene scene = new Scene(form, 400, 520);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // --- NOTIFICATION AND HELPER UTILITIES ---
    private void showNotification(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            
            // Apply current stylesheet to alerts dialog
            DialogPane dialogPane = alert.getDialogPane();
            if (mainScene != null) {
                dialogPane.getStylesheets().addAll(mainScene.getStylesheets());
            }
            
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
