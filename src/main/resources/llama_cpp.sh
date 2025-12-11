#!/bin/bash

echo "========================================"
echo "  CONFIGURANDO LLAMA.CPP PARA AMD RX 580 (ROCm)"
echo "========================================"

# Atualiza pacotes
sudo apt update && sudo apt install -y git build-essential cmake python3 python3-pip wget

echo "[1/5] Instalando dependências ROCm mínimas..."
sudo apt install -y hip-runtime-amd mesa-opencl-icd clinfo

echo "[2/5] Testando OpenCL..."
if clinfo | grep -q "RX"; then
  echo "GPU RX 580 detectada com OpenCL!"
else
  echo "Aviso: OpenCL não detectou GPU — HIP ainda deve funcionar com llama.cpp."
fi

echo "[3/5] Baixando llama.cpp com suporte ROCm..."
cd ~
if [ ! -d "llama.cpp" ]; then
  git clone https://github.com/ggerganov/llama.cpp
fi
cd llama.cpp
git pull

echo "[4/5] Compilando com suporte HIP/ROCm..."
make clean
make -j$(nproc) LLAMA_HIPBLAS=1

echo "[5/5] Criando pasta de modelos..."
mkdir -p ~/llama.cpp/models

echo "========================================"
echo "Instalação concluída com sucesso!"
echo
echo "Para iniciar o servidor, use EXACTAMENTE ESTE COMANDO:"
echo
echo "  cd ~/llama.cpp"
echo "  ./server \\"
echo "    --model ./models/llama-3.1-8b-instruct.Q4_K_M.gguf \\"
echo "    --port 1234 \\"
echo "    --host 0.0.0.0 \\"
echo "    --ctx-size 4096 \\"
echo "    --threads 24 \\"
echo "    --gpu-layers 30"
echo
echo "========================================"
echo "Substitua 'modelo.gguf' pelo nome do arquivo do seu modelo."
echo "Ex:   modelo.gguf  ->  llama-3.1-8b-instruct.Q4_K_M.gguf"
echo "========================================"


#
#cd ~/llama.cpp
 #./server \
 #  --model ./models/llama-3.1-8b-instruct.Q4_K_M.gguf \
 #  --port 1234 \
 #  --host 0.0.0.0 \
 #  --ctx-size 4096 \
 #  --threads 24 \
 #  --gpu-layers 30