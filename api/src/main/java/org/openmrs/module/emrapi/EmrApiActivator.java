/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.emrapi;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.LocationAttributeType;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Privilege;
import org.openmrs.Provider;
import org.openmrs.Role;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.LocationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.ProviderService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.datatype.FreeTextDatatype;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.ModuleActivator;
import org.openmrs.module.emrapi.account.AccountService;
import org.openmrs.module.emrapi.adt.EmrApiVisitAssignmentHandler;
import org.openmrs.module.emrapi.printer.PrinterDatatype;
import org.openmrs.module.emrapi.utils.GeneralUtils;
import org.openmrs.util.OpenmrsConstants;

/**
 * This class contains the logic that is run every time this module is either started or stopped.
 */
public class EmrApiActivator extends BaseModuleActivator {

    /**
     * @see ModuleActivator#contextRefreshed()
     */
    @Override
    public void contextRefreshed() {
        super.contextRefreshed();    //To change body of overridden methods use File | Settings | File Templates.

        ensurePrivilegeLevelRoles();
    }

    /**
     * Creates role "Privilege Level: Full" if does not exist
     *
     * @return
     */
    private void ensurePrivilegeLevelRoles() {
        UserService userService = Context.getUserService();
        AccountService accountService = Context.getService(AccountService.class);
        EmrApiProperties emrProperties = Context.getRegisteredComponents(EmrApiProperties.class).iterator().next();

        Role fullPrivilegeLevel = emrProperties.getFullPrivilegeLevel();
        if (fullPrivilegeLevel == null) {
            fullPrivilegeLevel = new Role();
            fullPrivilegeLevel.setRole(EmrApiConstants.PRIVILEGE_LEVEL_FULL_ROLE);
            fullPrivilegeLevel.setDescription(EmrApiConstants.PRIVILEGE_LEVEL_FULL_DESCRIPTION);
            fullPrivilegeLevel.setUuid(EmrApiConstants.PRIVILEGE_LEVEL_FULL_UUID);
            userService.saveRole(fullPrivilegeLevel);
        }

        for (Privilege candidate : accountService.getApiPrivileges()) {
            if (!fullPrivilegeLevel.hasPrivilege(candidate.getName())) {
                fullPrivilegeLevel.addPrivilege(candidate);
            }
        }
        userService.saveRole(fullPrivilegeLevel);
    }

    @Override
    public void started() {
        super.started();

        AdministrationService administrationService = Context.getAdministrationService();
        LocationService locationService = Context.getLocationService();
        ProviderService providerService = Context.getProviderService();
        PersonService personService = Context.getPersonService();

        createGlobalProperties(administrationService);
        createLocationAttributeTypes(locationService);
        createUnknownProvider(administrationService, providerService, personService);
    }

    private void createGlobalProperties(AdministrationService administrationService) {
        // When https://tickets.openmrs.org/browse/TRUNK-3773 is resolved, refactor this to refer to a bean by id
        GlobalProperty gp = administrationService.getGlobalPropertyObject(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER);
        if (gp == null) {
            gp = new GlobalProperty();
            gp.setProperty(OpenmrsConstants.GP_VISIT_ASSIGNMENT_HANDLER);
        }
        gp.setPropertyValue(EmrApiVisitAssignmentHandler.class.getName());
        administrationService.saveGlobalProperty(gp);
    }

    private void createLocationAttributeTypes(LocationService locationService) {
        LocationAttributeType defaultLabelPrinterAttributeType =
                locationService.getLocationAttributeTypeByUuid(EmrApiConstants.LOCATION_ATTRIBUTE_TYPE_DEFAULT_PRINTER.get("LABEL"));

        if (defaultLabelPrinterAttributeType == null) {
            defaultLabelPrinterAttributeType = new LocationAttributeType();
            defaultLabelPrinterAttributeType.setUuid(EmrApiConstants.LOCATION_ATTRIBUTE_TYPE_DEFAULT_PRINTER.get("LABEL"));
            defaultLabelPrinterAttributeType.setDatatypeClassname(PrinterDatatype.class.getName());
            defaultLabelPrinterAttributeType.setDatatypeConfig("LABEL");
            defaultLabelPrinterAttributeType.setMaxOccurs(1);
            defaultLabelPrinterAttributeType.setMinOccurs(0);
            defaultLabelPrinterAttributeType.setName("Default Label Printer");
            defaultLabelPrinterAttributeType.setDescription("The default label printer for this location");

            locationService.saveLocationAttributeType(defaultLabelPrinterAttributeType);
        } else {
            // if you change any field values above, you need to set them here, so existing servers can be updated
            boolean changed = GeneralUtils.setPropertyIfDifferent(defaultLabelPrinterAttributeType, "datatypeClassname", PrinterDatatype.class.getName());
            if (changed) {
                locationService.saveLocationAttributeType(defaultLabelPrinterAttributeType);
            }
        }

        LocationAttributeType defaultIdCardPrinterAttributeType =
                locationService.getLocationAttributeTypeByUuid(EmrApiConstants.LOCATION_ATTRIBUTE_TYPE_DEFAULT_PRINTER.get("ID_CARD"));

        if (defaultIdCardPrinterAttributeType == null) {
            defaultIdCardPrinterAttributeType = new LocationAttributeType();
            defaultIdCardPrinterAttributeType.setUuid(EmrApiConstants.LOCATION_ATTRIBUTE_TYPE_DEFAULT_PRINTER.get("ID_CARD"));
            defaultIdCardPrinterAttributeType.setDatatypeClassname(PrinterDatatype.class.getName());
            defaultIdCardPrinterAttributeType.setDatatypeConfig("ID_CARD");
            defaultIdCardPrinterAttributeType.setMaxOccurs(1);
            defaultIdCardPrinterAttributeType.setMinOccurs(0);
            defaultIdCardPrinterAttributeType.setName("Default ID card Printer");
            defaultIdCardPrinterAttributeType.setDescription("The default id card printer for this location");

            locationService.saveLocationAttributeType(defaultIdCardPrinterAttributeType);
        } else {
            // if you change any field values above, you need to set them here, so existing servers can be updated
            boolean changed = GeneralUtils.setPropertyIfDifferent(defaultIdCardPrinterAttributeType, "datatypeClassname", PrinterDatatype.class.getName());
            if (changed) {
                locationService.saveLocationAttributeType(defaultIdCardPrinterAttributeType);
            }
        }

        LocationAttributeType nameToPrintOnIdCardAttributeType =
                locationService.getLocationAttributeTypeByUuid(EmrApiConstants.LOCATION_ATTRIBUTE_TYPE_NAME_TO_PRINT_ON_ID_CARD);

        if (nameToPrintOnIdCardAttributeType == null) {
            nameToPrintOnIdCardAttributeType = new LocationAttributeType();
            nameToPrintOnIdCardAttributeType.setUuid(EmrApiConstants.LOCATION_ATTRIBUTE_TYPE_NAME_TO_PRINT_ON_ID_CARD);
            nameToPrintOnIdCardAttributeType.setDatatypeClassname(FreeTextDatatype.class.getName());
            nameToPrintOnIdCardAttributeType.setMaxOccurs(1);
            nameToPrintOnIdCardAttributeType.setMinOccurs(0);
            nameToPrintOnIdCardAttributeType.setName("Name to print on ID card");
            nameToPrintOnIdCardAttributeType.setDescription("The name to use when printing a location on an id card");

            locationService.saveLocationAttributeType(nameToPrintOnIdCardAttributeType);
        }
    }

    private void createUnknownProvider(AdministrationService adminService, ProviderService providerService, PersonService personService) {

        // see if the provider exists
        Provider provider = null;
        String providerUuid = adminService.getGlobalProperty(EmrApiConstants.GP_UNKNOWN_PROVIDER);

        if (StringUtils.isNotBlank(providerUuid)) {
            provider = providerService.getProviderByUuid(providerUuid);
        }

        // create the unknown provider if necessary
        if (provider == null) {
            Person unknownPerson = new Person();
            unknownPerson.setGender("F");
            PersonName unknownPersonName = new PersonName();
            unknownPersonName.setGivenName("Unknown");
            unknownPersonName.setFamilyName("Provider");
            unknownPerson.addName(unknownPersonName);

            personService.savePerson(unknownPerson);

            Provider unknownProvider = new Provider();
            unknownProvider.setPerson(unknownPerson);
            unknownProvider.setIdentifier("UNKNOWN");
            unknownProvider.setUuid("f9badd80-ab76-11e2-9e96-0800200c9a66");

            providerService.saveProvider(unknownProvider);

            // also set global property
            GlobalProperty unknownProviderUuid = adminService.getGlobalPropertyObject(EmrApiConstants.GP_UNKNOWN_PROVIDER);
            if (unknownProviderUuid == null) {
                unknownProviderUuid = new GlobalProperty(EmrApiConstants.GP_UNKNOWN_PROVIDER);
            }
            unknownProviderUuid.setPropertyValue("f9badd80-ab76-11e2-9e96-0800200c9a66");
                  adminService.saveGlobalProperty(unknownProviderUuid);

        }

    }

}
