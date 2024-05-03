# java.lang.IllegalArgumentException: Failure in creating proxy URL. Proxy port is required!

https://github.com/fabric8io/kubernetes-client/issues/5605

## Analysis

When a proxy is configured either through the environment variables (`HTTP_PROXY`, `HTTPS_PROXY`, `ALL_PROXY`) or via client configuration, the Kubernetes Client library will attempt to create a proxy URL using the proxy host and port.

If the port is not provided, the library will throw an `IllegalArgumentException` with the message `Failure in creating proxy URL. Proxy port is required!`.

The client relies on the URI class to parse the proxy URL, which requires the scheme/protocol to be present in the URL.

Since the user is providing an incomplete URI, the library treats the host as the scheme and the port as the host, which results in the mentioned exception.

## Related links

- https://github.com/fabric8io/kubernetes-client/issues/5605
- https://github.com/quarkusio/quarkus/discussions/37137
- https://github.com/fabric8io/kubernetes-client/pull/5976
