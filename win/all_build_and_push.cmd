set version=1.0.0

call build_and_push.cmd %version% raft-server
call build_and_push.cmd %version% raft-client