#! /bin/bash
cat > /etc/prometheus/prometheus.yml << EOF
$1
EOF
