package java_sdk;

public class IMCPayDomain implements IWXPayDomain {
    String domain;

    IMCPayDomain(String domain) {
        this.domain = domain;

    }

    @Override
    public void report(String domain, long elapsedTimeMillis, Exception ex) {

    }

    @Override
    public DomainInfo getDomain(WXPayConfig config) {
        return new DomainInfo(domain, true);


    }
}
