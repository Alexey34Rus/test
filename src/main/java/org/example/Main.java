package org.example;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Создание массива организаций с участниками
        Employee employee1 = new Employee();
        employee1.setFullName("John Doe");
        employee1.setDateOfBirth("1990-01-01");

        Employee employee2 = new Employee();
        employee2.setFullName("Jane Smith");
        employee2.setDateOfBirth("1991-02-03");

        List<Employee> employees = new ArrayList<>();
        employees.add(employee1);
        employees.add(employee2);

        Organization organization = new Organization();
        organization.setName("Sample Organization");
        organization.setCreationDate("2020-01-01");
        organization.setStatus(StatusOrg.Open);
        organization.setEmployees(employees);

        Organization organization2 = new Organization();
        organization2.setName("New Organization");
        organization2.setCreationDate("2022-02-02");
        organization2.setStatus(StatusOrg.Open);
        organization2.setEmployees(employees);

        Organization organization3 = new Organization();
        organization3.setName("Old Organization");
        organization3.setCreationDate("2023-03-03");
        organization3.setStatus(StatusOrg.Close);
        organization3.setEmployees(employees);

        List<Organization> organizationList = new ArrayList<>();
        organizationList.add(organization);
        organizationList.add(organization2);
        organizationList.add(organization3);

        OrganizationList organizations = new OrganizationList();
        organizations.setOrganizationList(organizationList);

        // Выполнение сериализации в XML
        try {
            File file = new File("organizations.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(OrganizationList.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(organizations, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        // Выполнение десериализации и вывод открытых организаций
        try {
            File file = new File("organizations.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(OrganizationList.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            OrganizationList deserializedOrganizations = (OrganizationList) unmarshaller.unmarshal(file);

            for (Organization org : deserializedOrganizations.getOrganizationList()) {
                if (org.getStatus().equals(StatusOrg.Open)) {
                    System.out.println("Открытая организация:");
                    System.out.println("Наименование: " + org.getName());
                    System.out.println("Дата создания: " + org.getCreationDate());
                    System.out.println("Статус: " + org.getStatus());
                    System.out.println();
                }
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        // Генерация XSD файлов для модели
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(OrganizationList.class);
            SchemaOutputResolver sor = new MySchemaOutputResolver();
            jaxbContext.generateSchema(sor);
        } catch (JAXBException | IOException e) {
            e.printStackTrace();
        }
    }

    static class MySchemaOutputResolver extends SchemaOutputResolver {
        @Override
        public Result createOutput(String namespaceUri, String suggestedFileName) {
            File file = new File(suggestedFileName);
            StreamResult result = new StreamResult(file);
            result.setSystemId(file.toURI().toString());
            return result;
        }
    }
}

