package org.openmrs.module.emrapi.disposition;


import org.codehaus.jackson.annotate.JsonProperty;
import org.openmrs.module.emrapi.disposition.actions.ClientSideAction;

import javax.validation.Valid;
import java.util.List;

public class Disposition {

    @JsonProperty
    private String uuid;

    @JsonProperty
    private String name;

    @JsonProperty
    private String conceptCode;

    /**
     * These should be the names of existing Spring beans
     */
    @JsonProperty
    private List<String> actions;

    @Valid
    @JsonProperty
    private List<ClientSideAction> clientSideActions;

    public Disposition(){

    }

    public Disposition(String uuid, String name, String conceptCode, List<String> actions, List<ClientSideAction> clientSideActions) {
        this.uuid = uuid;
        this.name = name;
        this.conceptCode = conceptCode;
        this.actions = actions;
        this.clientSideActions = clientSideActions;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Disposition that = (Disposition) o;

        if (actions != null ? !actions.equals(that.actions) : that.actions != null) return false;
        if (clientSideActions != null ? !clientSideActions.equals(that.clientSideActions) : that.clientSideActions != null)
            return false;
        if (!conceptCode.equals(that.conceptCode)) return false;
        if (!name.equals(that.name)) return false;
        if (!uuid.equals(that.uuid)) return false;

        return true;
    }

    public List<String> getActions() {
        return actions;
    }

    public List<ClientSideAction> getClientSideActions() {
        return clientSideActions;
    }

    public void setUuid(String uuid){
        this.uuid = uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public void setClientSideActions(List<ClientSideAction> clientSideActions) {
        this.clientSideActions = clientSideActions;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + conceptCode.hashCode();
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        result = 31 * result + (clientSideActions != null ? clientSideActions.hashCode() : 0);
        return result;
    }
}
