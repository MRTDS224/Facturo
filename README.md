# Facturo

Facturo est une puissante application locale de facturation développée en Java (avec JavaFX). Elle permet de concevoir rapidement des factures, de gérer vos clients et de configurer vos propres paramètres tout en stockant localement vos données sans dépendre du cloud.

## 🚀 Fonctionnalités principales
* **Édition de factures** : Ajout d'articles rapide et autocomplétion, fusion fine des produits identiques.
* **Génération de PDF** : Exports automatiques et de haute qualité des factures avec l'en-tête de votre propre entreprise.
* **Gestion des clients** : Carnet d'adresses client pour facturer en quelques clics.
* **Personnalisation** : Configurez votre nom, logo, adresse et informations de contact (sauvegarde locale).
* **Historique** : Retrouvez l'historique complet de vos factures.

## ⚙️ Prérequis
* **JDK (Java Development Kit)** version **21** ou ultérieure
* **Maven** (version 3.8+)

## 🛠️ Lancement en développement
Pour compiler et lancer directement le projet :
```bash
mvn clean javafx:run
```

## 📦 Compilation et packaging (Windows)
L'application peut être partagée et installée grâce à un installeur Windows.

1.  Générer l'exécutable via le plugin `launch4j-maven-plugin` :
    ```bash
    mvn clean package
    ```
2.  Compiler l'installeur :
    Si vous possédez **Inno Setup** installé localement, ouvrez le fichier `setup.iss` au chemin principal et compilez-le (ou utilisez la commande ligne `iscc setup.iss`).
    L'installeur serra généré dans le dossier `target\installer`.

## 📚 Stack technique
* **JavaFX 21** (Interface Graphique)
* **SQLite** (Base de données locale légère)
* **Apache PDFBox** (Structure et rendu des documents PDF)
* **ControlsFX**, **Ikonli** (Composants visuels)
