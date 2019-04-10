package util.info_execucao;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Bruno Cattany
 * @since 06/04/2019
 */
public class InformacaoMaquinaExecutora {

    private static final String MSG_INFO_NAO_POSSUI_INTERFACES_ATIVAS = "Não possui interfaces ativas ou não foi possível recuperá-las.";

    private final String versaoJava;
    private final String usuarioMaquina;
    private final String ipPublico;
    private final String ipLocal;
    private final String hostnameLocal;
    private final String displayNameLocal;
    private final String macLocal;
    private final List<InterfaceRedeAtiva> listaInterfacesRedeAtivas;

    InformacaoMaquinaExecutora(String versaoJava,
                               String usuarioMaquina,
                               String ipPublico,
                               String ipLocal,
                               String hostnameLocal,
                               String displayNameLocal,
                               String macLocal,
                               List<InterfaceRedeAtiva> listaInterfacesRedeAtivas) {
        this.versaoJava = versaoJava;
        this.usuarioMaquina = usuarioMaquina;
        this.ipPublico = ipPublico;
        this.ipLocal = ipLocal;
        this.hostnameLocal = hostnameLocal;
        this.displayNameLocal = displayNameLocal;
        this.macLocal = macLocal;
        this.listaInterfacesRedeAtivas = listaInterfacesRedeAtivas;
    }

    @Override
    public String toString() {
        String string = ""
                + "\tVersão do Java: " + System.getProperty("java.version")
                + "\n\tUsuário: " + usuarioMaquina
                + "\n\tIP Público: " + ipPublico
                + "\n\tIP Local: " + ipLocal
                + "\n\tHostname Local: " + hostnameLocal
                + "\n\tDisplayName Local: " + displayNameLocal
                + "\n\tMAC Local: " + macLocal;

        if (!listaInterfacesRedeAtivas.isEmpty()) {
            string += "\n\tInterfaces de rede ativas: " + listaInterfacesRedeAtivas + "\n\t}";
        } else {
            string += "\n\t" + MSG_INFO_NAO_POSSUI_INTERFACES_ATIVAS;
        }

        return string;
    }

    public String getVersaoJava() {
        return versaoJava;
    }

    public String getUsuarioMaquina() {
        return usuarioMaquina;
    }

    public String getIpPublico() {
        return ipPublico;
    }

    public String getIpLocal() {
        return ipLocal;
    }

    public String getHostnameLocal() {
        return hostnameLocal;
    }

    public String getDisplayNameLocal() {
        return displayNameLocal;
    }

    public String getMacLocal() {
        return macLocal;
    }

    public List<InterfaceRedeAtiva> getListaInterfacesRedeAtivas() {
        return listaInterfacesRedeAtivas;
    }

    static class InterfaceRedeAtiva {

        private Integer numeracao;
        private String displayName;
        private String name;
        private String mac;
        private boolean virtual;
        private boolean loopback;
        private List<String> hostAddress;

        InterfaceRedeAtiva(Integer numeracao, String displayName, String name, String mac,
                           boolean virtual, boolean loopback, List<String> hostAddress) {
            this.numeracao = numeracao;
            this.displayName = displayName;
            this.name = name;
            this.mac = mac;
            this.virtual = virtual;
            this.loopback = loopback;
            this.hostAddress = hostAddress;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("\n\t\t{\n\t\t\tInterface : ").append("#").append(numeracao)
                    .append("\n\t\t\tDisplay Name: ").append(displayName)
                    .append("\n\t\t\tName: ").append(name)
                    .append("\n\t\t\tMAC: ").append(mac)
                    .append("\n\t\t\tVirtual: ").append(virtual)
                    .append("\n\t\t\tLoopback: ").append(loopback)
                    .append("\n\t\t\tInterface Addresses: {");

            for (String h : hostAddress) {
                sb.append("\n\t\t\t\tHost Address: ").append(h);
            }

            sb.append("\n\t\t\t}");
            sb.append("\n\t\t}");
            sb.append("\n");

            return isNotBlank(sb.toString())
                    ? sb.toString().replaceAll("\n$", "") : null;
        }

    }
}
