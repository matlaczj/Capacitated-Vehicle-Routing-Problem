# Capacitated Vehicle Routing Problem

<img src="https://th.bing.com/th/id/OIG.QvzpFno3B2GxRQqceiGY?pid=ImgGn" width="300" height="300" style="display: block; margin: auto;">

## Overview

This is a program designed to address the optimal distribution of patients, taking into consideration factors such as the existence of roads between locations, the number of available beds in hospitals, and the proximity of the nearest hospital to the patient's location. The program provides a graphical interface for loading maps, patient lists, and visualizing the transportation of patients to the nearest available hospitals.

## Table of Contents

- [Introduction](#uber-heals)
- [General Description](#overview)
- [Functional Specification](/functional-specification.md)
- [Implementation Specification](/implementation-specification.md)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Technology Stack](#technology-stack)
- [Program Structure](#program-structure)
- [File Structure](#file-structure)
- [Contributing](#contributing)
- [Input Format](#input-format)

## Getting Started

### Prerequisites

- Windows 10
- Java Runtime Environment (JRE) version 8
- JavaFX for GUI
- JUnit 4 library

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/matlaczj/Capacitated-Vehicle-Routing-Problem
   cd Capacitated-Vehicle-Routing-Problem
   ```

2. Run the program:

   ```bash
   java -jar uber-heals.jar
   ```

## Usage

1. Launch the program using the provided JAR file.
2. Load maps and patient lists from input files or manually input data via the graphical interface.
3. Verify the correctness and consistency of loaded data.
4. Generate log files and calculate patient distribution to hospitals.
5. Visualize the distribution and observe any intuitive error messages.

For a detailed guide on program functionality and implementation, refer to the the reports catalog.

## Technology Stack

- Java 13 (Object-Oriented Programming)
- JavaFX for GUI
- JUnit 4 for testing

## Program Structure

The program is structured into classes, each responsible for specific tasks. Class diagrams are provided in the Implementation Specification.

## File Structure

```
/uber-heals
|-- functional-specification.md
|-- implementation-specification.md
|-- uber-heals.jar
|-- src
|   |-- com
|       |-- yourcompany
|           |-- uberheals
|               |-- Main.java
|               |-- ...
|-- test
|   |-- com
|       |-- yourcompany
|           |-- uberheals
|               |-- TestMain.java
|               |-- ...
|-- input-files
|   |-- map.txt
|   |-- patients.txt
|-- logs
|   |-- log_file.txt
|-- README.md
|-- .gitignore
```

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/new-feature`).
3. Make your changes and commit them (`git commit -m 'Add new feature'`).
4. Push to the branch (`git push origin feature/new-feature`).
5. Open a pull request.

## Input format

The input data for the "Uber Heals" program is specified in the functional specification and includes information about hospitals, objects (such as monuments), roads, and patients. Below is an explanation of the input data format for each category:

### Hospitals Data:

```plaintext
# Szpitale (id | nazwa | wsp. x | wsp. y | Liczba łóżek | Liczba wolnych łóżek)
1 | Szpital Wojewódzki nr 997 | 10 | 10 | 1000 | 100
2 | Krakowski Szpital Kliniczny | 100 | 120 | 999 | 99
3 | Pierwszy Szpital im. Prezesa RP | 120 | 130 | 99 | 0
4 | Drugi Szpital im. Naczelnika RP | 10 | 140 | 70 | 1
5 | Trzeci Szpital im. Króla RP | 140 | 10 | 996 | 0
```

- **id:** Unique identifier for the hospital.
- **nazwa:** Name of the hospital.
- **wsp. x:** X-coordinate of the hospital's location.
- **wsp. y:** Y-coordinate of the hospital's location.
- **Liczba łóżek:** Total number of beds in the hospital.
- **Liczba wolnych łóżek:** Number of available beds in the hospital.

### Objects Data:

```plaintext
# Obiekty (id | nazwa | wsp. x | wsp. y)
1 | Pomnik Wikipedii | -1 | 50
2 | Pomnik Fryderyka Chopina | 110 | 55
3 | Pomnik Anonimowego Przechodnia | 40 | 70
```

- **id:** Unique identifier for the object.
- **nazwa:** Name of the object.
- **wsp. x:** X-coordinate of the object's location.
- **wsp. y:** Y-coordinate of the object's location.

### Roads Data:

```plaintext
# Drogi (id | id_szpitala | id_szpitala | odległość)
1 | 1 | 2 | 700
2 | 1 | 4 | 550
3 | 1 | 5 | 800
4 | 2 | 3 | 300
5 | 2 | 4 | 550
6 | 3 | 5 | 600
7 | 4 | 5 | 750
```

- **id:** Unique identifier for the road.
- **id_szpitala:** Unique identifier of the starting hospital.
- **id_szpitala:** Unique identifier of the destination hospital.
- **odległość:** Distance between the hospitals along the road.

### Patients Data:

```plaintext
# Pacjenci (id | wsp. x | wsp.y)
1 | 20 | 20
2 | 99 | 105
3 | 23 | 40
```

- **id:** Unique identifier for the patient.
- **wsp. x:** X-coordinate of the patient's location.
- **wsp. y:** Y-coordinate of the patient's location.

This format uses a tabular structure with columns separated by the pipe symbol (`|`). Each row represents a different entity (hospital, object, road, or patient) with specific attributes or coordinates. The '#' symbol indicates a comment line, providing a human-readable description of the data category.