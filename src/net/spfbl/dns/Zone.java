/*
 * This file is part of SPFBL.
 *
 * SPFBL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SPFBL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SPFBL. If not, see <http://www.gnu.org/licenses/>.
 */

package net.spfbl.dns;

import java.io.Serializable;
import net.spfbl.core.Core;
import net.spfbl.core.ProcessException;
import net.spfbl.dnsbl.ServerDNSBL;
import net.spfbl.whois.Domain;

/**
 * Zona DNS dos serviços.
 *
 * @author Leandro Carlos Rodrigues <leandro@spfbl.net>
 */
public class Zone implements Serializable, Comparable<Zone> {

    private static final long serialVersionUID = 1L;

    private final String hostname;
    private String message;
    private final Type type;
    
    public enum Type {
        DNSBL,
        DNSWL
    }
    
    public Zone(ServerDNSBL zone) {
        this.hostname = zone.getHostName();
        this.message = zone.getMessage();
        this.type = Type.DNSBL;
    }

    public Zone(Type type, String hostname, String message) {
        this.hostname = hostname;
        this.message = message;
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isHostName(String hostname) {
        return this.hostname.equals(Domain.normalizeHostname(hostname, true));
    }

    public String getHostName() {
        return hostname;
    }
    
    public String getTypeName() {
        return type.name();
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isDNSBL() {
        return type == Type.DNSBL;
    }
    
    public boolean isDNSWL() {
        return type == Type.DNSWL;
    }

    public String getMessage(String token) {
        try {
            if (type == Type.DNSBL) {
                String url = Core.getDNSBLURL(token);
                if (url == null) {
                    return message;
                } else {
                    return url;
                }
            } else {
                return message;
            }
        } catch (ProcessException ex) {
            return message;
        }
    }

    @Override
    public int compareTo(Zone other) {
        if (other == null) {
            return -1;
        } else {
            return this.hostname.compareTo(other.hostname);
        }
    }

    @Override
    public String toString() {
        return hostname;
    }

    public String extractDomain(String host) {
        if ((host = Domain.normalizeHostname(host, true)) == null) {
            return null;
        } else if (host.equals(this.hostname)) {
            return null;
        } else if (host.endsWith(this.hostname)) {
            int index = host.length() - this.hostname.length();
            String result = host.substring(0, index);
            if (Domain.isHostname(result)) {
                return result;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
