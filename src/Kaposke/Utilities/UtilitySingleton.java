package Kaposke.Utilities;

/*
* Eu não achei um jeito melhor de passar os valores de caminhos da classe AgentManager para a classe dos Agentes.
* Por isso criei este singleton. Sei que é porco, mas eu estou com pressa.
* */

// TODO: STOP USING THIS SINGLETON.
public class UtilitySingleton {

    // Singleton Stuff
    private static UtilitySingleton i;

    public static UtilitySingleton getInstance() {
        if(i == null)
            i = new UtilitySingleton();
        return i;
    }

    // Smelly stuff
    private String arffPath;
    private String settingsPath;

    public void setArffPath(String arffPath) {
        this.arffPath = arffPath;
    }

    public String getArffPath() {
        return arffPath;
    }

    public void setSettingsPath(String settingsPath) {
        this.settingsPath = settingsPath;
    }

    public String getSettingsPath() {
        return settingsPath;
    }
}
