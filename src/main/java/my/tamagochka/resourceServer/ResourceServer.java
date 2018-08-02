package my.tamagochka.resourceServer;

import resources.TestResource;

public class ResourceServer implements ResourceServerMBean {

    private TestResource testResource;

    public void setTestResource(TestResource testResource) {
        this.testResource = testResource;
    }

    @Override
    public String getname() {
        return testResource.getName();
    }

    @Override
    public int getage() {
        return testResource.getAge();
    }
}
