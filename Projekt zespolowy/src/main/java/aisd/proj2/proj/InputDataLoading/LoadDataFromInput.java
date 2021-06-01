package aisd.proj2.proj.InputDataLoading;

import aisd.proj2.proj.MapElements.Hospital;
import aisd.proj2.proj.MapElements.Patient;
import aisd.proj2.proj.MapElements.Road;
import aisd.proj2.proj.MapElements.Thing;

import java.io.*;
import java.util.*;

public class LoadDataFromInput {
    private String mapFileName;
    private String patientsFileName;
    private BufferedReader mapFileBR;
    private BufferedReader patientsFileBR;
    private int nHospitals;
    private int nThings;
    private int nRoads;
    private int nPatients;
    private ArrayList<Hospital> hospitals;
    private ArrayList<Thing> things;
    private ArrayList<Road> roads;
    private ArrayList<Patient> patients;
    private int firstShadowHospIdx;
    public static final int MAP_MODE = 0;
    public static final int PATIENTS_MODE = 1;
    public static final int BOTH_MODE = 2;
    private int mode;

    public static final String inputFileNameReg = "^[\\w,\\s-]+\\.txt$";
    public static final String numberOrZeroReg = "(0|([1-9][0-9]*))";
    public static final String decimalPartReg = "(\\.[0-9]+)?";
    public static final String minusOrNotReg = "(-)?";
    public static final String positiveDoubleNumberReg = numberOrZeroReg + decimalPartReg;
    public static final String possiblyNegativeDoubleNumberReg = minusOrNotReg + numberOrZeroReg + decimalPartReg;
    public static final String nameReg = "[^\\|]+";
    public static final String separatorReg = " *\\| *";
    public static final String startReg = "^ *";
    public static final String endReg = " *$";

    public static final String hospitalsBodyReg = startReg + numberOrZeroReg + separatorReg + nameReg
            + (separatorReg + possiblyNegativeDoubleNumberReg).repeat(2)
            + (separatorReg + numberOrZeroReg).repeat(2) + endReg;

    public static final String thingsBodyReg = startReg + numberOrZeroReg + separatorReg + nameReg +
            (separatorReg + possiblyNegativeDoubleNumberReg).repeat(2) + endReg;

    public static final String roadsBodyReg = startReg + (numberOrZeroReg + separatorReg).repeat(3)
            + positiveDoubleNumberReg + endReg;

    public static final String patientsBodyReg = startReg + numberOrZeroReg
            + (separatorReg + possiblyNegativeDoubleNumberReg).repeat(2) + endReg;

    public static final String isHeaderReg = "^ *#.*$";
    public static final String headerReg = "^ *# *";

    public static final String hospitalsHeaderReg = "Szpitale \\(id \\| nazwa \\| wsp. x \\| wsp. y \\| Liczba łóżek \\| Liczba wolnych łóżek\\)";
    public static final String thingsHeaderReg = "Obiekty \\(id \\| nazwa \\| wsp. x \\| wsp. y\\)";
    public static final String roadsHeaderReg = "Drogi \\(id \\| id_szpitala \\| id_szpitala \\| odległość\\)";
    public static final String patientsHeaderReg = "Pacjenci \\(id \\| wsp. x \\| wsp.y\\)";

    public LoadDataFromInput(String mapFileName, String patientsFileName, int mode) {
        this.mapFileName = mapFileName;
        this.patientsFileName = patientsFileName;

        if (!(mode == BOTH_MODE || mode == MAP_MODE || mode == PATIENTS_MODE)) {
            throw new IllegalArgumentException("Invalid mode. Please change mode of LoadDataFromInput object.");
        }
        this.mode = mode;

        hospitals = new ArrayList<>();
        things = new ArrayList<>();
        roads = new ArrayList<>();
        patients = new ArrayList<>();
        loadDataFromInput();
        firstShadowHospIdx = hospitals.size();
    }

    private void loadDataFromInput() {
        checkInputFilesNames();

        createBufferedReaders();
        checkIfBufferedReadersAreNull();

        checkInputFilesFormats();

        resetBufferedReaders();

        loadDataToFields();

        closeBufferedReaders();

        checkInputFileConsistency();

        normalizeIdx();
    }

    private void checkInputFilesNames() {
        if (mode == BOTH_MODE || mode == MAP_MODE) {
            if (!(mapFileName.matches(inputFileNameReg))) {
                throw new IllegalArgumentException("Map file has wrong format.\n" +
                        "Please remove any forbidden signs that are not accepted by appropriate regex.");
            }
        }
        if (mode == BOTH_MODE || mode == PATIENTS_MODE) {
            if (!(patientsFileName.matches(inputFileNameReg))) {
                throw new IllegalArgumentException("Patients file has wrong format.\n" +
                        "Please remove any forbidden signs that are not accepted by appropriate regex.");
            }
        }
        System.out.println("Checked input names.");
    }

    private void checkInputFilesFormats() {
        try {
            if (mode == BOTH_MODE || mode == MAP_MODE) {
                checkMapFileFormat();
            }
            if (mode == BOTH_MODE || mode == PATIENTS_MODE) {
                checkPatientsFileFormat();
            }

        } catch (IOException e) {
            System.out.println("There was an error during reading input file.");
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Checked input format.");
    }

    private void checkMapFileFormat() throws IOException {
        String line;

        line = loadNextNotEmptyLine(mapFileBR);

        checkIfLineIsHeader(line, hospitalsHeaderReg);

        line = loadNextNotEmptyLine(mapFileBR);

        while (!line.matches(isHeaderReg)) {
            checkIfLineIsPartOfBody(line, hospitalsBodyReg);
            line = loadNextNotEmptyLine(mapFileBR);
            checkIfLineIsNull(line);
        }

        checkIfLineIsHeader(line, thingsHeaderReg);

        line = loadNextNotEmptyLine(mapFileBR);

        while (!line.matches(isHeaderReg)) {
            checkIfLineIsPartOfBody(line, thingsBodyReg);
            line = loadNextNotEmptyLine(mapFileBR);
            checkIfLineIsNull(line);
        }

        checkIfLineIsHeader(line, roadsHeaderReg);

        line = loadNextNotEmptyLine(mapFileBR);

        while (line != null) {
            checkIfLineIsPartOfBody(line, roadsBodyReg);
            line = loadNextNotEmptyLine(mapFileBR);
        }

    }

    private void checkPatientsFileFormat() throws IOException {
        String line;

        line = loadNextNotEmptyLine(patientsFileBR);

        checkIfLineIsHeader(line, patientsHeaderReg);

        line = loadNextNotEmptyLine(patientsFileBR);

        while (line != null) {
            checkIfLineIsPartOfBody(line, patientsBodyReg);
            line = loadNextNotEmptyLine(patientsFileBR);
        }
    }

    private void checkIfLineIsHeader(String line, String header) {
        if (!line.matches(headerReg + header)) {
            throw new IllegalArgumentException("Wrong input file format.\n" +
                    "Expected the line to be a header but it is not.");
        }
    }

    private void checkIfLineIsPartOfBody(String line, String body) {
        if (!line.matches(body)) {
            throw new IllegalArgumentException("Wrong input file format.\n" +
                    "Expected the line to be a part of body but it is not.\n" +
                    "The line:\n" + line);
        }
    }

    private void checkIfLineIsNull(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Wrong input file format.\n" +
                    "Didn't expect a null line in middle of input file.");
        }
    }

    private void loadDataToFields() {
        try {
            if (mode == BOTH_MODE || mode == MAP_MODE) {
                loadNextNotEmptyLine(mapFileBR);
                loadHospitals();
                loadThings();
                loadRoads();
            }
            if (mode == BOTH_MODE || mode == PATIENTS_MODE) {
                loadNextNotEmptyLine(patientsFileBR);
                loadPatients();
            }

        } catch (IOException e) {
            System.out.println("There was an error during reading of input file.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Loaded data to fields.");
    }

    private void loadHospitals() throws IOException {
        String[] buffer = new String[6];
        String line = loadNextNotEmptyLine(mapFileBR);

        int idx;
        String name;
        double x;
        double y;
        int allBeds;
        int freeBeds;

        while (!line.matches(isHeaderReg)) {
            buffer = line.split("\\|");

            idx = Integer.parseInt(buffer[0].trim());
            name = buffer[1].trim();
            x = Double.parseDouble(buffer[2].trim());
            y = Double.parseDouble(buffer[3].trim());
            allBeds = Integer.parseInt(buffer[4].trim());
            freeBeds = Integer.parseInt(buffer[5].trim());

            hospitals.add(new Hospital(idx, name, x, y, allBeds, freeBeds));

            line = loadNextNotEmptyLine(mapFileBR);
        }

        nHospitals = hospitals.size();
    }

    private void loadThings() throws IOException {
        String[] buffer = new String[4];
        String line = loadNextNotEmptyLine(mapFileBR);

        int idx;
        String name;
        double x;
        double y;

        while (!line.matches(isHeaderReg)) {
            buffer = line.split("\\|");

            idx = Integer.parseInt(buffer[0].trim());
            name = buffer[1].trim();
            x = Double.parseDouble(buffer[2].trim());
            y = Double.parseDouble(buffer[3].trim());

            things.add(new Thing(idx, name, x, y));

            line = loadNextNotEmptyLine(mapFileBR);
        }

        nThings = things.size();
    }

    private void loadRoads() throws IOException {
        String[] buffer = new String[4];
        String line = loadNextNotEmptyLine(mapFileBR);

        int idx;
        int firstHospIdx;
        int secondHospIdx;
        double distance;

        if (line == null) {
            nRoads = 0;
            return;
        }

        while (!line.matches(isHeaderReg)) {
            buffer = line.split("\\|");

            idx = Integer.parseInt(buffer[0].trim());
            firstHospIdx = Integer.parseInt(buffer[1].trim());
            secondHospIdx = Integer.parseInt(buffer[2].trim());
            distance = Double.parseDouble(buffer[3].trim());

            roads.add(new Road(idx, firstHospIdx, secondHospIdx, distance));

            line = loadNextNotEmptyLine(mapFileBR);

            if (line == null) {
                break;
            }
        }

        nRoads = roads.size();
    }

    private void loadPatients() throws IOException {
        String[] buffer = new String[3];
        String line = loadNextNotEmptyLine(patientsFileBR);

        int idx;
        double x;
        double y;

        if (line == null) {
            nPatients = 0;
            return;
        }

        while (!line.matches(isHeaderReg)) {
            buffer = line.split("\\|");

            idx = Integer.parseInt(buffer[0].trim());
            x = Double.parseDouble(buffer[1].trim());
            y = Double.parseDouble(buffer[2].trim());

            patients.add(new Patient(idx, x, y));

            line = loadNextNotEmptyLine(patientsFileBR);

            if (line == null) {
                break;
            }
        }

        nPatients = patients.size();
    }

    private void checkInputFileConsistency() {

        if (mode == BOTH_MODE || mode == MAP_MODE) {
            sortHospitalsAndThingsByNames();
            checkForDuplicateNamesInHospitalsAndThings();
        }

        sortEverythingByIdx();
        checkForDuplicateIdx();

        if (mode == BOTH_MODE || mode == MAP_MODE) {
            checkForRoadsFromAndToTheSameHospital();
            checkForDuplicateHospitalsIdxPairsInRoads();
            sortRoadsBy("hospIdx");
            checkForRoadsBetweenNonexistentHospitals();
            checkIfThereAreMoreFreeBedsThanAllBeds();
        }

        System.out.println("Checked input data's consistency.");
    }

    private void checkIfThereAreMoreFreeBedsThanAllBeds() {
        for (int i = 0; i < nHospitals; i++) {
            Hospital currHosp = hospitals.get(i);
            if (currHosp.getFreeBeds() > currHosp.getAllBeds()) {
                throw new IllegalArgumentException("\nThere is a hospital with number of free beds greater than of all beds.");
            }
        }
    }

    private void checkForDuplicateNamesInHospitalsAndThings() {
        String previous;
        String current;

        for (int i = 1; i < nHospitals; i++) {
            previous = hospitals.get(i - 1).getName();
            current = hospitals.get(i).getName();
            if (current.equals(previous)) {
                throw new IllegalArgumentException("The file contains inconsistent data.\n" +
                        "There are duplicate Hospital object names: " + current);
            }
        }

        for (int i = 1; i < nThings; i++) {
            previous = things.get(i - 1).getName();
            current = things.get(i).getName();
            if (current.equals(previous)) {
                throw new IllegalArgumentException("The file contains inconsistent data.\n" +
                        "There are duplicate Thing object names: " + current);
            }
        }
    }

    private void checkForDuplicateIdx() {
        int previous;
        int current;

        if (mode == BOTH_MODE || mode == MAP_MODE) {
            for (int i = 1; i < nHospitals; i++) {
                previous = hospitals.get(i - 1).getIdx();
                current = hospitals.get(i).getIdx();
                if (current == previous) {
                    throw new IllegalArgumentException("The file contains inconsistent data.\n" +
                            "There are duplicate Hospital object indexes: " + current);
                }
            }
            for (int i = 1; i < nThings; i++) {
                previous = things.get(i - 1).getIdx();
                current = things.get(i).getIdx();
                if (current == previous) {
                    throw new IllegalArgumentException("The file contains inconsistent data.\n" +
                            "There are duplicate Thing object indexes: " + current);
                }
            }
            for (int i = 1; i < nRoads; i++) {
                previous = roads.get(i - 1).getIdx();
                current = roads.get(i).getIdx();
                if (current == previous) {
                    throw new IllegalArgumentException("The file contains inconsistent data.\n" +
                            "There are duplicate Road object indexes: " + current);
                }
            }
        }
        if (mode == BOTH_MODE || mode == PATIENTS_MODE) {
            for (int i = 1; i < nPatients; i++) {
                previous = patients.get(i - 1).getIdx();
                current = patients.get(i).getIdx();
                if (current == previous) {
                    throw new IllegalArgumentException("The file contains inconsistent data.\n" +
                            "There are duplicate Patient object indexes: " + current);
                }
            }
        }

    }

    private void checkForRoadsFromAndToTheSameHospital() {
        int firstHospIdx;
        int secondHospIdx;

        for (int i = 0; i < nRoads; i++) {
            firstHospIdx = roads.get(i).getFirstHospIdx();
            secondHospIdx = roads.get(i).getSecondHospIdx();

            if (firstHospIdx == secondHospIdx) {
                throw new IllegalArgumentException("The file contains inconsistent data.\n" +
                        "There is road that starts in the same Hospital in which it ends:\n" +
                        "Its index: " + firstHospIdx);
            }
        }
    }

    private void checkForDuplicateHospitalsIdxPairsInRoads() {
        int prevFirstHospIdx;
        int prevSecondHospIdx;
        int currFirstHospIdx;
        int currSecondHospIdx;

        for (int i = 1; i < nRoads; i++) {
            prevFirstHospIdx = roads.get(i - 1).getFirstHospIdx();
            prevSecondHospIdx = roads.get(i - 1).getSecondHospIdx();
            currFirstHospIdx = roads.get(i).getFirstHospIdx();
            currSecondHospIdx = roads.get(i).getSecondHospIdx();

            if ((prevFirstHospIdx == currFirstHospIdx && prevSecondHospIdx == currSecondHospIdx) ||
                    (prevFirstHospIdx == currSecondHospIdx && prevSecondHospIdx == currFirstHospIdx)) {
                throw new IllegalArgumentException("The file contains inconsistent data.\n" +
                        "There are duplicate hospital index pairs in Road objects:\nfirst hospital idx: " +
                        currFirstHospIdx + " second hospital idx: " + currSecondHospIdx);
            }
        }

    }

    private void checkForRoadsBetweenNonexistentHospitals() {
        int firstHospIdx;
        int secondHospIdx;

        for (int i = 0; i < nRoads; i++) {
            firstHospIdx = roads.get(i).getFirstHospIdx();
            secondHospIdx = roads.get(i).getSecondHospIdx();
            if (isHospitalNonexistent(firstHospIdx) || isHospitalNonexistent(secondHospIdx)) {
                throw new IllegalArgumentException("The file contains inconsistent data.\n" +
                        "One of the Hospitals in Road object is nonexistent:\n" +
                        "first hospital index: " + firstHospIdx + " second hospital index: " + secondHospIdx);
            }

        }
    }

    private boolean isHospitalNonexistent(int hospIdx) {
        boolean isHospitalExistent = false;

        for (int i = 0; i < nHospitals; i++) {
            if (hospIdx == hospitals.get(i).getIdx()) {
                isHospitalExistent = true;
            }
        }

        return !isHospitalExistent;
    }

    private void normalizeIdx() {

        if (mode == BOTH_MODE || mode == MAP_MODE) {
            int firstHospIdx;
            int secondHospIdx;
            Hashtable<Integer, Integer> hospIdxChangeHash = new Hashtable<>();
            Hashtable<Integer, Integer> thinIdxChangeHash = new Hashtable<>();
            Hashtable<Integer, Integer> roadIdxChangeHash = new Hashtable<>();

            for (int i = 0; i < nHospitals; i++) {
                hospIdxChangeHash.put(hospitals.get(i).getIdx(), i);
            }

            for (int i = 0; i < nThings; i++) {
                thinIdxChangeHash.put(things.get(i).getIdx(), i);
            }

            for (int i = 0; i < nRoads; i++) {
                roadIdxChangeHash.put(roads.get(i).getIdx(), i);
            }

            for (int i = 0; i < nRoads; i++) {
                firstHospIdx = roads.get(i).getFirstHospIdx();
                secondHospIdx = roads.get(i).getSecondHospIdx();

                roads.get(i).setFirstHospIdx(hospIdxChangeHash.get(firstHospIdx));
                roads.get(i).setSecondHospIdx(hospIdxChangeHash.get(secondHospIdx));
            }

            for (int i = 0; i < nHospitals; i++) {
                int oldHospIdx = hospitals.get(i).getIdx();
                hospitals.get(i).setIdx(hospIdxChangeHash.get(oldHospIdx));
            }

            for (int i = 0; i < nThings; i++) {
                int oldThinIdx = things.get(i).getIdx();
                things.get(i).setIdx(thinIdxChangeHash.get(oldThinIdx));
            }

            for (int i = 0; i < nRoads; i++) {
                int oldRoadIdx = roads.get(i).getIdx();
                roads.get(i).setIdx(roadIdxChangeHash.get(oldRoadIdx));
            }
        }

        if (mode == BOTH_MODE || mode == PATIENTS_MODE) {
            int oldPatIdx;
            Hashtable<Integer, Integer> patIdxChangeHash = new Hashtable<>();

            for (int i = 0; i < nPatients; i++) {
                patIdxChangeHash.put(patients.get(i).getIdx(), i);
            }

            for (int i = 0; i < nPatients; i++) {
                oldPatIdx = patients.get(i).getIdx();
                patients.get(i).setIdx(patIdxChangeHash.get(oldPatIdx));
            }
        }

    }

    private String loadNextNotEmptyLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();

        while (isLineEmpty(line)) {
            line = bufferedReader.readLine();
        }

        return line;
    }

    public static boolean isLineEmpty(String line) {
        if (line == null) {
            return false;
        }

        if (line.strip() == "") {
            return true;
        } else {
            return false;
        }

    }

    public void printFields() {
        if (mode == BOTH_MODE || mode == MAP_MODE) {
            System.out.println("h o s p i t a l s\n");
            System.out.println(Arrays.deepToString(hospitals.toArray()) + "\n");
            System.out.println("t h i n g s\n");
            System.out.println(Arrays.deepToString(things.toArray()) + "\n");
            System.out.println("r o a d s\n");
            System.out.println(Arrays.deepToString(roads.toArray()) + "\n");
        }
        if (mode == BOTH_MODE || mode == PATIENTS_MODE) {
            System.out.println("p a t i e n t s\n");
            System.out.println(Arrays.deepToString(patients.toArray()) + "\n");
        }
    }

    private BufferedReader createBufferedReaderForInputFile(String inputFileName) {
        try {
            File file = new File("data" + File.separator + "input" + File.separator + inputFileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            return bufferedReader;
        } catch (FileNotFoundException | IllegalArgumentException e) {
            System.out.println("Could not find the file inside 'data/input/' catalog.");
            e.printStackTrace();
            return null;
        }
    }

    private void createBufferedReaders() {
        if (mode == BOTH_MODE || mode == MAP_MODE) {
            mapFileBR = createBufferedReaderForInputFile(mapFileName);
        }
        if (mode == BOTH_MODE || mode == PATIENTS_MODE) {
            patientsFileBR = createBufferedReaderForInputFile(patientsFileName);
        }

    }

    private void checkIfBufferedReadersAreNull() {
        if (mode == BOTH_MODE || mode == MAP_MODE) {
            if (mapFileBR == null) {
                System.exit(1);
            }
        }
        if (mode == BOTH_MODE || mode == PATIENTS_MODE) {
            if (patientsFileBR == null) {
                System.exit(1);
            }
        }
    }

    private void closeBufferedReaders() {
        try {
            if (mode == BOTH_MODE || mode == MAP_MODE) {
                mapFileBR.close();
            }
            if (mode == BOTH_MODE || mode == PATIENTS_MODE) {
                patientsFileBR.close();
            }

        } catch (IOException e) {
            System.out.println("There was an error closing file.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void resetBufferedReaders() {
        closeBufferedReaders();
        createBufferedReaders();
        checkIfBufferedReadersAreNull();
    }

    private void sortHospitalsAndThingsByNames() {
        sortHospitalsBy("name");
        sortThingsBy("name");
    }

    private void sortEverythingByIdx() {
        if (mode == BOTH_MODE || mode == MAP_MODE) {
            sortHospitalsBy("idx");
            sortThingsBy("idx");
            sortRoadsBy("idx");
        }
        if (mode == BOTH_MODE || mode == PATIENTS_MODE) {
            sortPatientsByIdx();
        }
    }

    private void sortHospitalsBy(String by) {
        if (by.equals("name")) {
            Collections.sort(hospitals, new Comparator<Hospital>() {
                @Override
                public int compare(Hospital h1, Hospital h2) {
                    return h1.getName().compareTo(h2.getName());
                }
            });
        } else if (by.equals("idx")) {
            Collections.sort(hospitals, new Comparator<Hospital>() {
                @Override
                public int compare(Hospital h1, Hospital h2) {
                    return h1.getIdx() - h2.getIdx();
                }
            });
        } else {
            throw new IllegalArgumentException("hospitals list cannot be sorted by this attribute because " +
                    "its either non-existent or not allowed.");
        }
    }

    private void sortThingsBy(String by) {
        if (by.equals("name")) {
            Collections.sort(things, new Comparator<Thing>() {
                @Override
                public int compare(Thing t1, Thing t2) {
                    return t1.getName().compareTo(t2.getName());
                }
            });
        } else if (by.equals("idx")) {
            Collections.sort(things, new Comparator<Thing>() {
                @Override
                public int compare(Thing t1, Thing t2) {
                    return t1.getIdx() - t2.getIdx();
                }
            });
        } else {
            throw new IllegalArgumentException("things list cannot be sorted by this attribute because " +
                    "its either non-existent or not allowed.");
        }
    }

    private void sortRoadsBy(String by) {
        if (by.equals("idx")) {
            Collections.sort(roads, new Comparator<Road>() {
                @Override
                public int compare(Road r1, Road r2) {
                    return r1.getIdx() - r2.getIdx();
                }
            });
        } else if (by.equals("hospIdx")) {
            Collections.sort(roads, new Comparator<Road>() {
                @Override
                public int compare(Road r1, Road r2) {
                    if (r1.getFirstHospIdx() == r2.getFirstHospIdx()) {
                        return r1.getSecondHospIdx() - r2.getSecondHospIdx();
                    } else {
                        return r1.getFirstHospIdx() - r2.getFirstHospIdx();
                    }
                }
            });
        } else {
            throw new IllegalArgumentException("roads list cannot be sorted by this attribute because " +
                    "its either non-existent or not allowed.");
        }
    }

    private void sortPatientsByIdx() {
        Collections.sort(patients, new Comparator<Patient>() {
            @Override
            public int compare(Patient p1, Patient p2) {
                return p1.getIdx() - p2.getIdx();
            }
        });
    }

    public String getMapFileName() {
        return mapFileName;
    }

    public String getPatientsFileName() {
        return patientsFileName;
    }

    public int getnHospitals() {
        return nHospitals;
    }

    public int getnThings() {
        return nThings;
    }

    public int getnRoads() {
        return nRoads;
    }

    public int getnPatients() {
        return nPatients;
    }

    public ArrayList<Hospital> getHospitals() {
        return hospitals;
    }

    public ArrayList<Thing> getThings() {
        return things;
    }

    public ArrayList<Road> getRoads() {
        return roads;
    }

    public ArrayList<Patient> getPatients() {
        return patients;
    }
}
