Works on CAS V6.0.*

## Apereo CAS Config

### In cas.properties

```
cas.authn.mfa.globalProviderId=mfa-esup-nfc
```

Or use another Multifactor Authentication Trigger :

https://apereo.github.io/cas/6.0.x/mfa/Configuring-Multifactor-Authentication-Triggers.html

In cas.properties, add also translations (or/and customize esup-nfc-cas-mfa _messages) :
cas.messageBundle.baseNames=classpath:custom_messages,classpath:messages,classpath:esupnfccas_messages

### In esupnfc.properties (classpath:esupnfc.properties", "file:/var/cas/config/esupnfc.properties", "file:/opt/cas/config/esupnfc.properties", "file:/etc/cas/config/esupnfc.properties", "file:${cas.standalone.configurationDirectory}/esupnfc.properties) : 

```
# ESUP-NFC-TAG-SERVER IP (ESUP-NFC-TAG-SERVER calls ESUP-NFC-TAG-CAS-MFA CAS REST API on CAS server)
esupnfc.esupNfcTagServersIP[0]=10.0.0.1

# autologin true/false :
# * if false, user has to copy/past code from esup-nfc-tag-droid or esup-nfc-tag-desktop
# * if true no need to copy/past code thanks to a http long poll on CAS (easier for user but less secure)
esupnfc.autologin=true

# the name of room ('salle') displayed to the user on esup-nfc-tag-droid or esup-nfc-tag-desktop
esupnfc.esupNfcTagLocationName=CAS
```


## ESUP-NFC-TAG-SERVER Config

```
<bean id="casExtApi" class="org.esupportail.nfctag.service.api.impl.AppliExtRestWs">
   <property name="isTagableUrl" value="https://cas.univ-ville.fr/esupnfc/wsrest/isTagable"/>
   <property name="validateTagUrl" value="https://cas.univ-ville.fr/esupnfc/wsrest/validateTag"/>
   <property name="getLocationsUrl" value="https://cas.univ-ville.fr/esupnfc/wsrest/locations"/>
    <!--<property name="displayUrl" value="https://cas.univ-ville.fr/esupnfc/wsrest/display"/>-->
   <property name="description" value="Web Service Cas Dev MFA"/>
   <property name="backgroundColor" value="rgb(98, 0, 238)"/>
</bean>
```

Config displayUrl so that user can see ESUP NFC CAS MFA token == only if esupnfc.autologin is set up to false.

