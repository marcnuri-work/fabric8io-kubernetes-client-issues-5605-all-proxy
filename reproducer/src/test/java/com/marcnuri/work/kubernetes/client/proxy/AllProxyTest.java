package com.marcnuri.work.kubernetes.client.proxy;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.VersionInfo;
import io.fabric8.kubernetes.client.http.TestStandardHttpClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AllProxyTest {

  private TestStandardHttpClientFactory httpClientFactory;

  @BeforeEach
  void setUp() {
    httpClientFactory = new TestStandardHttpClientFactory();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "proxy.example.com:8080", // Fails on 6.12, should work after fix
    "http://proxy.example.com:8080"
  })
  void test(String url) {
    final KubernetesClient kc = new KubernetesClientBuilder()
      .withHttpClientFactory(httpClientFactory)
      .editOrNewConfig()
      .withHttpProxy(url)
      .withHttpsProxy(url)
      .withNoProxy("no-proxy.example.com")
      .endConfig()
      .build();
    httpClientFactory.expect("/version", 200, "{\"major\":\"1\"}");
    final VersionInfo version = kc.getKubernetesVersion();
    assertEquals("1", version.getMajor());
    final InetSocketAddress proxyAddress = httpClientFactory.getInstances().peek().newBuilder().getProxyAddress();
    assertEquals("proxy.example.com", proxyAddress.getHostName());
    assertEquals(8080, proxyAddress.getPort());
  }
}
