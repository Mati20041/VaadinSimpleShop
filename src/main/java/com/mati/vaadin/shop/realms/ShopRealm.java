package com.mati.vaadin.shop.realms;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.ldap.JndiLdapRealm;
import org.apache.shiro.realm.ldap.LdapContextFactory;
import org.apache.shiro.subject.PrincipalCollection;
import javax.naming.NamingException;
import java.util.HashSet;
import java.util.Set;


public class ShopRealm extends JndiLdapRealm {

        protected AuthorizationInfo queryForAuthorizationInfo(PrincipalCollection principals,
                                                              LdapContextFactory ldapContextFactory) throws NamingException {

            String username = (String) getAvailablePrincipal(principals);

            Set<String> roleNames = new HashSet<String>();
            if("admin".equals(username)) {
                roleNames.add("super_user");
            }else if("henio".equals(username)) {
                roleNames.add("super_user");
            } else if ("wielkihenio".equals(username)) {
                roleNames.add("super_user");
            }else {
                roleNames.add("normal_user");
            }


            return buildAuthorizationInfo(roleNames);
        }

        protected AuthorizationInfo buildAuthorizationInfo(Set<String> roleNames) {
            return new SimpleAuthorizationInfo(roleNames);
        }

    }
