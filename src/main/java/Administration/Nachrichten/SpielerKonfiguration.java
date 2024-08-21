package Administration.Nachrichten;

import GUI.ESpielertyp;

public class SpielerKonfiguration {
    public SpielerKonfiguration(String name, ESpielertyp spielertyp) {
        this.name = name;
        this.spielertyp = spielertyp;
    }
    private final String name;
    private final ESpielertyp spielertyp;
    
    public String getName() {
        return name;
    }

    public ESpielertyp getSpielertyp() {
        return spielertyp;
    }
}
