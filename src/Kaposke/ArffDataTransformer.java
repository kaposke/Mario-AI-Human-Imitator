package Kaposke;

import Kaposke.Utilities.ActionDictionary;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class ArffDataTransformer {

    private static void convertToGenericJump(File folder) throws IOException {
        for (final File fileEntry : folder.listFiles()) {

            if (fileEntry.getName().endsWith(".arff")) {
                System.out.println("Found arff file");
                BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
                BufferedWriter writer = new BufferedWriter(new FileWriter(folder + "/GeneralJump" + fileEntry.getName()));

                String line = "";
                do {
                    writer.write(line);
                    writer.newLine();
                } while (!(line = reader.readLine()).equals("@attribute isMarioCarrying { false , true }"));

                String actionString = "@attribute Action { ";
                List<String> possibleActions = ActionDictionary.getAllActionsPossibilitiesButJump();
                for (int i = 0; i < possibleActions.size(); i++) {
                    actionString += possibleActions.get(i);
                    if (i != possibleActions.size() - 1)
                        actionString += " , ";
                }
                actionString += " }";

                writer.write(actionString);
                writer.newLine();
                writer.write("@attribute Jump { false , true }");
                writer.newLine();
                writer.newLine();
                writer.write("@data");
                writer.newLine();

                // Read until the data part
                while (!(line = reader.readLine()).equals("@data")) ;

                // Data reading, converting and writing
                while ((line = reader.readLine()) != null) {
                    List<String> data = Arrays.asList(line.split(", "));
                    boolean[] action = new boolean[6];
                    for (int i = 0; i < data.size(); i++) {
                        if (i < 367)
                            writer.write(data.get(i) + ", ");
                        if (i == 367) {
                            action[0] = data.get(i).equals("true");
                        } else if (i == 368) {
                            action[1] = data.get(i).equals("true");
                        } else if (i == 369) {
                            action[2] = data.get(i).equals("true");
                        } else if (i == 370) {
                            action[3] = data.get(i).equals("true");
                        } else if (i == 371) {
                            action[3] = data.get(i).equals("true");
                        } else if (i == 372) {
                            action[3] = data.get(i).equals("true");
                        }
                    }
                    writer.write(ActionDictionary.buildActionStringWithoutJump(action) + ", " + (action[3] ? "true" : "false"));
                    writer.newLine();
                }
                writer.flush();
                writer.close();
            }

        }
    }

    private static void ConvertToVerbal(File originalFolder, File folder) throws IOException {
        if (!folder.exists())
            folder.mkdir();
        for (final File fileEntry : originalFolder.listFiles()) {

            if (fileEntry.getName().endsWith(".arff")) {
                System.out.println("Found arff file");
                BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
                BufferedWriter writer = new BufferedWriter(new FileWriter(folder + "/" + fileEntry.getName()));

                String line = "";
                do {
                    writer.write(line);
                    writer.newLine();
                } while (!(line = reader.readLine()).equals("@attribute isMarioCarrying { false , true }"));

                writer.write(line);
                writer.newLine();

                String actionString = "@attribute Action { ";
                List<String> possibleActions = ActionDictionary.getAllActionsPossibilities();
                for (int i = 0; i < possibleActions.size(); i++) {
                    actionString += possibleActions.get(i);
                    if (i != possibleActions.size() - 1)
                        actionString += " , ";
                }
                actionString += " }";

                writer.write(actionString);
                writer.newLine();
                writer.newLine();
                writer.write("@data");
                writer.newLine();

                // Read until the data part
                while (!(line = reader.readLine()).equals("@data")) ;

                // Data reading, converting and writing
                while ((line = reader.readLine()) != null) {
                    List<String> data = Arrays.asList(line.split(", "));
                    boolean[] action = new boolean[6];
                    for (int i = 0; i < data.size(); i++) {
                        if (i < 365)
                            writer.write(data.get(i) + ", ");
                        if (i == 365) {
                            action[0] = data.get(i).equals("true");
                        } else if (i == 366) {
                            action[1] = data.get(i).equals("true");
                        } else if (i == 367) {
                            action[2] = data.get(i).equals("true");
                        } else if (i == 368) {
                            action[3] = data.get(i).equals("true");
                        } else if (i == 369) {
                            action[4] = data.get(i).equals("true");
                        } else if (i == 370) {
                            action[5] = data.get(i).equals("true");
                        }
                    }
                    writer.write(ActionDictionary.buildActionString(action));
                    writer.newLine();
                }
                writer.flush();
                writer.close();
            }

        }
    }

    private static void convertToGenericRemoveMode(File originalFolder, File folder) throws IOException {
        if (!folder.exists())
            folder.mkdir();
        for (final File fileEntry : originalFolder.listFiles()) {

            if (fileEntry.getName().endsWith(".arff")) {
                System.out.println("Found arff file");
                BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
                BufferedWriter writer = new BufferedWriter(new FileWriter(folder + "/" + fileEntry.getName()));

                String line = "";
                do {
                    writer.write(line);
                    writer.newLine();
                } while (!(line = reader.readLine()).equals("@attribute isMarioCarrying { false , true }"));

                writer.write(line);
                writer.newLine();

                String actionString = "@attribute Action { ";
                List<String> possibleActions = ActionDictionary.getAllActionsPossibilities();
                for (int i = 0; i < possibleActions.size(); i++) {
                    actionString += possibleActions.get(i);
                    if (i != possibleActions.size() - 1)
                        actionString += " , ";
                }
                actionString += " }";

                writer.write(actionString);
                writer.newLine();
                writer.newLine();
                writer.write("@data");
                writer.newLine();

                // Read until the data part
                while (!(line = reader.readLine()).equals("@data")) ;

                // Data reading, converting and writing
                while ((line = reader.readLine()) != null) {
                    List<String> data = Arrays.asList(line.split(", "));
                    boolean[] action = new boolean[6];
                    for (int i = 0; i < data.size(); i++) {
                        if(i == 362) {
                            writer.write("0, ");
                            continue;
                        }
                        if (i < 367)
                            writer.write(data.get(i) + ", ");
                        if (i == 367) {
                            action[0] = data.get(i).equals("true");
                        } else if (i == 368) {
                            action[1] = data.get(i).equals("true");
                        } else if (i == 369) {
                            action[2] = data.get(i).equals("true");
                        } else if (i == 370) {
                            action[3] = data.get(i).equals("true");
                        } else if (i == 371) {
                            action[4] = data.get(i).equals("true");
                        } else if (i == 372) {
                            action[5] = data.get(i).equals("true");
                        }
                    }
                    writer.write(ActionDictionary.buildActionString(action));
                    writer.newLine();
                }
                writer.flush();
                writer.close();
            }

        }
    }

    private static void removeIdleOccurencies(File originalFolder, File folder) throws IOException {
        if (!folder.exists())
            folder.mkdir();
        for (final File fileEntry : originalFolder.listFiles()) {

            if (fileEntry.getName().endsWith(".arff")) {
                System.out.println("Found arff file");
                BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
                BufferedWriter writer = new BufferedWriter(new FileWriter(folder + "/" + fileEntry.getName()));

                String line = "";

                while (!(line = reader.readLine()).equals("@data")) {
                    writer.write(line);
                    writer.newLine();
                }

                writer.write(line);
                writer.newLine();

                while ((line = reader.readLine()) != null) {
                    List<String> data = Arrays.asList(line.split(", "));
                    boolean[] action = new boolean[6];
                    for (int i = 0; i < data.size(); i++) {
                        if (i == 367) {
                            action[0] = data.get(i).equals("true");
                        } else if (i == 368) {
                            action[1] = data.get(i).equals("true");
                        } else if (i == 369) {
                            action[2] = data.get(i).equals("true");
                        } else if (i == 370) {
                            action[3] = data.get(i).equals("true");
                        } else if (i == 371) {
                            action[4] = data.get(i).equals("true");
                        } else if (i == 372) {
                            action[5] = data.get(i).equals("true");
                        }
                    }
                    boolean idle = true;
                    for (boolean a : action) {
                        if (a) {
                            idle = false;
                        }
                    }
                    if (!idle) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
                writer.flush();
                writer.close();
            }

        }
    }

    private static void RemoveInitialIdleOccurencies(File originalFolder, File folder) throws IOException {
        if (!folder.exists())
            folder.mkdir();
        for (final File fileEntry : originalFolder.listFiles()) {

            if (fileEntry.getName().endsWith(".arff")) {
                System.out.println("Found arff file");
                BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
                BufferedWriter writer = new BufferedWriter(new FileWriter(folder + "/" + fileEntry.getName()));

                String line = "";

                while (!(line = reader.readLine()).equals("@data")) {
                    writer.write(line);
                    writer.newLine();
                }

                writer.write(line);
                writer.newLine();
                boolean foundFirstMove = false;
                while ((line = reader.readLine()) != null) {
                    List<String> data = Arrays.asList(line.split(", "));
                    boolean[] action = new boolean[6];
                    for (int i = 0; i < data.size(); i++) {
                        if (i == 367) {
                            action[0] = data.get(i).equals("true");
                        } else if (i == 368) {
                            action[1] = data.get(i).equals("true");
                        } else if (i == 369) {
                            action[2] = data.get(i).equals("true");
                        } else if (i == 370) {
                            action[3] = data.get(i).equals("true");
                        } else if (i == 371) {
                            action[4] = data.get(i).equals("true");
                        } else if (i == 372) {
                            action[5] = data.get(i).equals("true");
                        }
                    }
                    boolean idle = true;
                    for (boolean a : action) {
                        if (a) {
                            idle = false;
                        }
                    }
                    if(foundFirstMove) {
                        idle = false;
                    }
                    if (!idle) {
                        foundFirstMove = true;
                        writer.write(line);
                        writer.newLine();
                    }
                }
                writer.flush();
                writer.close();
            }

        }
    }

    private static void CombineFiles(File originalFolder, File folder) throws IOException {
        if (!folder.exists())
            folder.mkdir();
        boolean firstArff = true;
        for (int i = 0; i < originalFolder.listFiles().length; i++) {
            File fileEntry = originalFolder.listFiles()[i];
            if (fileEntry.getName().endsWith(".arff")) {
                BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
                BufferedWriter writer = new BufferedWriter(new FileWriter(folder + "/CombinedData.arff", true));

                String line = "";
                // Write attributes
                while (!(line = reader.readLine()).equals("@data")) {
                    if (firstArff) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
                if (firstArff) {
                    writer.write(line);
                    writer.newLine();
                }
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
                writer.close();
                firstArff = false;
            }
        }
    }

    private static void RemoveMode(File originalFolder, File folder) throws IOException {
        if (!folder.exists())
            folder.mkdir();
        for (final File fileEntry : originalFolder.listFiles()) {

            if (fileEntry.getName().endsWith(".arff")) {
                System.out.println("Found arff file");
                BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
                BufferedWriter writer = new BufferedWriter(new FileWriter(folder + "/" + fileEntry.getName()));

                String line = "";

                while (!(line = reader.readLine()).equals("@data")) {
                    if(line.equals("@attribute marioMode numeric"))
                        continue;
                    writer.write(line);
                    writer.newLine();
                }

                writer.write(line);
                writer.newLine();
                boolean foundFirstMove = false;
                while ((line = reader.readLine()) != null) {
                    List<String> data = Arrays.asList(line.split(", "));
                    StringBuilder newData = new StringBuilder();
                    for (int i = 0; i < data.size(); i++) {
                        if(i != 362) {
                            newData.append(data.get(i));
                            if (i != data.size() - 1)
                                newData.append(", ");
                        }
                    }
                    writer.write(newData.toString());
                    writer.newLine();;
                }
                writer.flush();
                writer.close();
            }

        }
    }

    private static void RemoveStatusConvertMode(File originalFolder, File folder) throws IOException {
        if (!folder.exists())
            folder.mkdir();
        for (final File fileEntry : originalFolder.listFiles()) {

            if (fileEntry.getName().endsWith(".arff")) {
                System.out.println("Found arff file");
                BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
                BufferedWriter writer = new BufferedWriter(new FileWriter(folder + "/" + fileEntry.getName()));

                String line = "";

                while (!(line = reader.readLine()).equals("@data")) {
                    if(line.equals("@attribute marioStatus numeric"))
                        continue;
                    if(line.equals("@attribute marioMode numeric")) {
                        writer.write("@attribute marioMode { fire , large , small }");
                        writer.newLine();
                        continue;
                    }
                    writer.write(line);
                    writer.newLine();
                }

                writer.write(line);
                writer.newLine();
                boolean foundFirstMove = false;
                while ((line = reader.readLine()) != null) {
                    List<String> data = Arrays.asList(line.split(", "));
                    StringBuilder newData = new StringBuilder();
                    for (int i = 0; i < data.size(); i++) {
                        if(i != 361) {
                            if(i == 362) {
                                int mode = Integer.parseInt(data.get(i));
                                newData.append(mode == 2 ? "fire" : mode == 1 ? "large" : "small");
                            } else {
                                newData.append(data.get(i));
                            }
                            if (i != data.size() - 1)
                                newData.append(", ");
                        }
                    }
                    writer.write(newData.toString());
                    writer.newLine();;
                }
                writer.flush();
                writer.close();
            }

        }
    }

    private static void RemoveStatusAndMode(File originalFolder, File folder) throws IOException {
        if (!folder.exists())
            folder.mkdir();
        for (final File fileEntry : originalFolder.listFiles()) {

            if (fileEntry.getName().endsWith(".arff")) {
                System.out.println("Found arff file");
                BufferedReader reader = new BufferedReader(new FileReader(fileEntry));
                BufferedWriter writer = new BufferedWriter(new FileWriter(folder + "/" + fileEntry.getName()));

                String line = "";

                while (!(line = reader.readLine()).equals("@data")) {
                    if(line.equals("@attribute marioStatus numeric"))
                        continue;
                    if(line.equals("@attribute marioMode numeric")) {
                        continue;
                    }
//                    if(line.equals("@attribute isMarioOnGround { false , true }")) {
//                        continue;
//                    }
//                    if(line.equals("@attribute isMarioAbleToJump { false , true }")) {
//                        continue;
//                    }
                    writer.write(line);
                    writer.newLine();
                }

                writer.write(line);
                writer.newLine();
                boolean foundFirstMove = false;
                while ((line = reader.readLine()) != null) {
                    List<String> data = Arrays.asList(line.split(", "));
                    StringBuilder newData = new StringBuilder();
                    for (int i = 0; i < data.size(); i++) {
                        if(i != 361 && i != 362
                                //&& i != 363 && i != 364)
                                ){

                            newData.append(data.get(i));

                            if (i != data.size() - 1)
                                newData.append(", ");
                        }
                    }
                    writer.write(newData.toString());
                    writer.newLine();;
                }
                writer.flush();
                writer.close();
            }

        }
    }

    public static void main(String[] args) throws IOException {
        File originalFolder = new File("C:/Users/1513 X-MXTI/IdeaProjects/TuringTestSrc/PlayerRecordings");

        File noStatusAndModeFolder = new File("C:/Users/1513 X-MXTI/IdeaProjects/TuringTestSrc/PlayerRecordingsNoStatusAndMode");
        RemoveStatusAndMode(originalFolder, noStatusAndModeFolder);

//        File noInitialIdleFolder = new File("C:/Users/1513 X-MXTI/IdeaProjects/TuringTestSrc/PlayerRecordingsNoInitialIdle");
//        RemoveInitialIdleOccurencies(originalFolder, noInitialIdleFolder);

        File verbalFolder = new File("C:/Users/1513 X-MXTI/IdeaProjects/TuringTestSrc/PlayerRecordingsVerbal");
        ConvertToVerbal(noStatusAndModeFolder, verbalFolder);

//        File verbalNoInitialIdleFolder = new File("C:/Users/1513 X-MXTI/IdeaProjects/TuringTestSrc/PlayerRecordingsVerbalNoInitialIdleFolder");
//        ConvertToVerbal(noInitialIdleFolder, verbalNoInitialIdleFolder);

        File combinedFolder = new File("C:/Users/1513 X-MXTI/IdeaProjects/TuringTestSrc/PlayerRecordingsCombined");
        CombineFiles(noStatusAndModeFolder, combinedFolder);

//        File noInitialIdleCombinedFolder = new File("C:/Users/1513 X-MXTI/IdeaProjects/TuringTestSrc/PlayerRecordingsNoInitialIdleCombined");
//        CombineFiles(noInitialIdleFolder, noInitialIdleCombinedFolder);

        File verbalCombinedFolder = new File("C:/Users/1513 X-MXTI/IdeaProjects/TuringTestSrc/PlayerRecordingsVerbalCombined");
        CombineFiles(verbalFolder, verbalCombinedFolder);

//        File verbalNoInitialIdleCombinedFolder = new File("C:/Users/1513 X-MXTI/IdeaProjects/TuringTestSrc/PlayerRecordingsVerbalNoInitialIdleCombined");
//        CombineFiles(verbalNoInitialIdleFolder, verbalNoInitialIdleCombinedFolder);
    }

}
