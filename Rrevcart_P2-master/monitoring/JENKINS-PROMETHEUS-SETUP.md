# Jenkins Prometheus Plugin Setup

## Step 1 — Install the Plugin

Go to: Manage Jenkins → Plugins → Available plugins
Search: "Prometheus metrics"
Install: "Prometheus metrics" by Jenkins Prometheus Plugin developers
Restart Jenkins after install.

## Step 2 — Enable the metrics endpoint

Go to: Manage Jenkins → System → Prometheus
- Check: "Expose Prometheus metrics"
- Path: leave as default (/prometheus)
- Save

Jenkins will now expose metrics at: http://localhost:8080/prometheus

## Step 3 — Verify it works

Open in browser or run:
  curl http://localhost:8080/prometheus

You should see lines like:
  jenkins_builds_total_build_count_total ...
  jenkins_executors_count ...
  jenkins_memory_heap_used ...

## Step 4 — Prometheus scrapes Jenkins automatically

The prometheus.yml already has this job configured:
  - job_name: 'jenkins'
    metrics_path: '/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8080']

host.docker.internal resolves to your Windows host IP from inside Docker,
so Prometheus (running in Docker) can reach Jenkins (running on Windows host).

## Step 5 — View Jenkins dashboard in Grafana

1. Open http://localhost:3000 (admin/admin)
2. Go to Dashboards → Browse → RevCart folder
3. Open "Jenkins CI/CD" dashboard

Panels available:
  - Jenkins Up/Down status
  - Total builds count
  - Failed builds count
  - Build success rate gauge
  - Build duration over time
  - Build rate per minute
  - Jenkins JVM heap usage
  - Jenkins executor usage
