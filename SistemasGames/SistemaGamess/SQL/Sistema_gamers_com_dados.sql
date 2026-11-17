-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 17/11/2026 às 01:52
-- Versão do servidor: 10.11.16-MariaDB
-- Versão do PHP: 8.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `sistemagames`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `campeonatos`
--

CREATE TABLE `campeonatos` (
  `id_campeonato` int(11) NOT NULL,
  `nome` varchar(120) NOT NULL,
  `jogo` varchar(80) NOT NULL,
  `data_inicio` date NOT NULL,
  `data_fim` date NOT NULL,
  `premio` decimal(12,2) DEFAULT 0.00,
  `status` enum('INSCRICOES_ABERTAS','EM_ANDAMENTO','ENCERRADO') DEFAULT 'INSCRICOES_ABERTAS',
  `criado_em` timestamp NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `campeonatos`
--

INSERT INTO `campeonatos` (`id_campeonato`, `nome`, `jogo`, `data_inicio`, `data_fim`, `premio`, `status`, `criado_em`) VALUES
(1, 'Major', 'Counter Strike 2', '2026-11-11', '2026-11-12', 150.00, 'ENCERRADO', '2026-11-12 01:13:15'),
(2, 'Camp Valorant', 'valorant', '2026-11-16', '2026-11-17', 1000000.00, 'INSCRICOES_ABERTAS', '2026-11-16 19:08:50'),
(3, 'teste002', 'FIVEM', '2026-11-16', '2026-11-17', 1000000.00, 'INSCRICOES_ABERTAS', '2026-11-16 19:10:55');

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `campeonatos`
--
ALTER TABLE `campeonatos`
  ADD PRIMARY KEY (`id_campeonato`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `campeonatos`
--
ALTER TABLE `campeonatos`
  MODIFY `id_campeonato` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
