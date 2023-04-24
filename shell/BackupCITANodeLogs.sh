#! /bin/bash
tail -$1 /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-auth.log >>  /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-auth_$1.log;
tail -$1 /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-bft.log >>  /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-bft_$1.log;
tail -$1 /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-chain.log >>  /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-chain_$1.log;
tail -$1 /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-executor.log >>  /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-executor_$1.log;
tail -$1 /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-forever.log >>  /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-forever_$1.log;
tail -$1 /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-jsonrpc.log >>  /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-jsonrpc_$1.log;
tail -$1 /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-network.log >>  /home/cmb/cita/cmb-cita-runtime/$2/logs/cita-network_$1.log;
