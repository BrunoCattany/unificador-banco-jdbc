package util.info_execucao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Bruno Cattany
 * @since 06/04/2019
 */
public class InformacaoMaquinaExecutoraFactory {

    private final static InformacaoMaquinaExecutoraFactory INSTANCE = new InformacaoMaquinaExecutoraFactory();

    private static final String MSG_FALHA_RECUPERAR = "??? - Não foi possível recuperar ";
    private static final String MSG_FALHA_IP_PUBLICO = MSG_FALHA_RECUPERAR + "o IP Público";
    private static final String MSG_FALHA_IP_LOCAL = MSG_FALHA_RECUPERAR + "o IP Local";
    private static final String MSG_FALHA_HOST_NAME = MSG_FALHA_RECUPERAR + "o nome do HOST.";
    private static final String MSG_FALHA_DISPLAY_NAME = MSG_FALHA_RECUPERAR + "o display name.";
    private static final String MSG_FALHA_MAC = MSG_FALHA_RECUPERAR + "o MAC - Medium Access Control.";

    private static final String URL_SERVICO_RECUPERADOR_IP_PACTO = "http://app.pactosolucoes.com.br/ip/v2.php";
    private static final String URL_SERVICO_RECUPERACOR_IP = "http://bot.whatismyipaddress.com";

    private InformacaoMaquinaExecutoraFactory() {
    }

    public static InformacaoMaquinaExecutoraFactory getInstance() {
        return INSTANCE;
    }

    public InformacaoMaquinaExecutora getInformacoesMaquinaExecutora() {
        final String versaoJava = System.getProperty("java.version");
        final String usuarioMaquina = System.getProperty("user.name");
        final String ipPublico = getIpPublico();
        final String ipLocal = getIpLocal();
        final String hostnameLocal = getHostname();
        final String displayNameLocal = getDisplayName();
        final String macLocal = getMAC();

        final List<InformacaoMaquinaExecutora.InterfaceRedeAtiva> interfaceRedeAtivas = getAllInterfacesActive();

        return new InformacaoMaquinaExecutora(
                versaoJava,
                usuarioMaquina,
                ipPublico,
                ipLocal,
                hostnameLocal,
                displayNameLocal,
                macLocal,
                interfaceRedeAtivas
        );
    }

    private String getIpLocal() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return MSG_FALHA_IP_LOCAL;
        }
    }

    private String getIpPublico() {
        try {
            String ip = _getIP(URL_SERVICO_RECUPERADOR_IP_PACTO);
            return isNotBlank(ip) ? ip : _getIP(URL_SERVICO_RECUPERACOR_IP);
        } catch (Exception e) {
            try {
                return _getIP(URL_SERVICO_RECUPERACOR_IP);
            } catch (IOException ex) {
                return MSG_FALHA_IP_PUBLICO;
            }
        }
    }

    private String _getIP(String url) throws IOException {
        BufferedReader sc = new BufferedReader(
                new InputStreamReader(new URL(url).openStream())
        );

        return sc.readLine().trim();
    }

    private String getHostname() {
        try {
            final String hostName = InetAddress.getLocalHost().getHostName();

            return hostName;
        } catch (UnknownHostException e) {
            return MSG_FALHA_HOST_NAME;
        }
    }

    private String getDisplayName() {
        try {
            return NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getDisplayName();
        } catch (SocketException e) {
            return MSG_FALHA_DISPLAY_NAME;
        } catch (UnknownHostException e) {
            return MSG_FALHA_DISPLAY_NAME;
        }
    }

    private String getMAC() {
        try {
            NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            return _getMAC(network.getHardwareAddress());
        } catch (SocketException e) {
            return MSG_FALHA_MAC;
        } catch (UnknownHostException e) {
            return MSG_FALHA_MAC;
        }
    }

    private String _getMAC(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }

        return isNotBlank(sb.toString()) ? sb.toString() : MSG_FALHA_MAC;
    }

    private List<InformacaoMaquinaExecutora.InterfaceRedeAtiva> getAllInterfacesActive() {
        List<InformacaoMaquinaExecutora.InterfaceRedeAtiva> interfacesAtivas = new LinkedList<InformacaoMaquinaExecutora.InterfaceRedeAtiva>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            int cont = 0;
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isUp()) {

                    List<String> hostAddress = new LinkedList<String>();

                    for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                        hostAddress.add(ia.getAddress().getHostAddress());
                    }

                    final InformacaoMaquinaExecutora.InterfaceRedeAtiva interfaceRedeAtiva = new InformacaoMaquinaExecutora.InterfaceRedeAtiva(
                            ++cont,
                            ni.getDisplayName(),
                            ni.getName(),
                            _getMAC(ni.getHardwareAddress()),
                            ni.isVirtual(),
                            ni.isLoopback(),
                            hostAddress
                    );

                    interfacesAtivas.add(interfaceRedeAtiva);
                }
            }

            return interfacesAtivas;
        } catch (SocketException e) {
            return Collections.emptyList();
        }
    }

}
