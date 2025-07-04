USE [master]
GO
/****** Object:  Database [temp_clinic_booking1]    Script Date: 5/19/2025 1:06:15 AM ******/
CREATE DATABASE [temp_clinic_booking1]
GO
ALTER DATABASE [temp_clinic_booking1] SET COMPATIBILITY_LEVEL = 150
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [temp_clinic_booking1].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [temp_clinic_booking1] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET ARITHABORT OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [temp_clinic_booking1] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [temp_clinic_booking1] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET  DISABLE_BROKER 
GO
ALTER DATABASE [temp_clinic_booking1] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [temp_clinic_booking1] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [temp_clinic_booking1] SET  MULTI_USER 
GO
ALTER DATABASE [temp_clinic_booking1] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [temp_clinic_booking1] SET DB_CHAINING OFF 
GO
ALTER DATABASE [temp_clinic_booking1] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [temp_clinic_booking1] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [temp_clinic_booking1] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [temp_clinic_booking1] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
ALTER DATABASE [temp_clinic_booking1] SET QUERY_STORE = OFF
GO
USE [temp_clinic_booking1]
GO
/****** Object:  Table [dbo].[accounts]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[accounts](
	[account_id] [bigint] IDENTITY(1,1) NOT NULL,
	[email] [varchar](255) NOT NULL,
	[password] [varchar](255) NOT NULL,
	[role] [varchar](10) NOT NULL,
	[created_at] [datetime] NULL,
	[updated_at] [datetime] NULL,
	[reset_password_token] [varchar](255) NULL,
	[reset_token_expiry] [datetime] NULL,
	[name_account] [varchar](50) NOT NULL,
	[provider] [varchar](50) NULL,
 CONSTRAINT [PK__accounts__46A222CD34DF6E89] PRIMARY KEY CLUSTERED 
(
	[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[appointments]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[appointments](
	[appointment_id] [bigint] IDENTITY(1,1) NOT NULL,
	[patient_id] [bigint] NOT NULL,
	[schedule_id] [bigint] NOT NULL,
	[booking_time] [datetime] NOT NULL,
	[status] [varchar](10) NULL,
	[deposit_amount] [decimal](10, 2) NULL,
	[created_at] [datetime] NULL,
	[updated_at] [datetime] NULL,
	[period] [int] NULL,
PRIMARY KEY CLUSTERED 
(
	[appointment_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[carts]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[carts](
	[cart_id] [bigint] IDENTITY(1,1) NOT NULL,
	[patient_id] [bigint] NOT NULL,
	[product_id] [bigint] NOT NULL,
	[quantity] [int] NOT NULL,
	[created_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[cart_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[doctors]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[doctors](
	[doctor_id] [bigint] IDENTITY(1,1) NOT NULL,
	[account_id] [bigint] NOT NULL,
	[specialty] [nvarchar](100) NULL,
	[address] [nvarchar](255) NULL,
	[license] [nvarchar](100) NULL,
	[experience] [text] NULL,
	[membership_id] [bigint] NULL,
	[status] [varchar](10) NULL,
	[created_at] [datetime] NULL,
	[updated_at] [datetime] NULL,
	[birthday] [datetime] NULL,
	[fullname] [nvarchar](250) NULL,
	[gender] [nvarchar](50) NULL,
	[startdate] [datetime] NULL,
	[enddate] [datetime] NULL,
	[phone_number] [varchar](20) NULL,
	[image] [varchar](255) NULL,
 CONSTRAINT [PK__doctors__F3993564B4A5FEE0] PRIMARY KEY CLUSTERED 
(
	[doctor_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[memberships]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[memberships](
	[membership_id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](50) NOT NULL,
	[price] [decimal](10, 2) NOT NULL,
	[description] [nvarchar](255) NULL,
	[created_at] [datetime] NULL,
	[updated_at] [datetime] NULL,
 CONSTRAINT [PK__membersh__CAE49DDD9E1C72D7] PRIMARY KEY CLUSTERED 
(
	[membership_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[order_items]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order_items](
	[order_item_id] [bigint] IDENTITY(1,1) NOT NULL,
	[order_id] [bigint] NOT NULL,
	[product_id] [bigint] NOT NULL,
	[quantity] [int] NOT NULL,
	[price] [decimal](10, 2) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[order_item_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[order_membership]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order_membership](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[membership_id] [bigint] NULL,
	[doctor_id] [bigint] NULL,
	[count] [bigint] NULL,
	[totalprice] [decimal](10, 2) NULL,
	[status] [nchar](10) NULL,
	[date] [datetime] NULL,
 CONSTRAINT [PK_order_membership] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[orders]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[orders](
	[order_id] [bigint] IDENTITY(1,1) NOT NULL,
	[patient_id] [bigint] NOT NULL,
	[total_amount] [decimal](10, 2) NOT NULL,
	[order_date] [datetime] NULL,
	[status] [varchar](10) NULL,
	[created_at] [datetime] NULL,
	[updated_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[order_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[products]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[products](
	[product_id] [bigint] IDENTITY(1,1) NOT NULL,
	[doctor_id] [bigint] NOT NULL,
	[name] [nvarchar](255) NOT NULL,
	[price] [decimal](10, 2) NOT NULL,
	[description] [nvarchar](max) NULL,
	[created_at] [datetime] NULL,
	[updated_at] [datetime] NULL,
	[image] [varchar](255) NULL,
	[count] [bigint] NULL,
 CONSTRAINT [PK__products__47027DF517162D7E] PRIMARY KEY CLUSTERED 
(
	[product_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[reviews]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[reviews](
	[review_id] [bigint] IDENTITY(1,1) NOT NULL,
	[patient_id] [bigint] NOT NULL,
	[doctor_id] [bigint] NOT NULL,
	[rating] [int] NOT NULL,
	[comment] [text] NULL,
	[created_at] [datetime] NULL,
	[status] [varchar](10) NULL,
PRIMARY KEY CLUSTERED 
(
	[review_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[schedules]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[schedules](
	[schedule_id] [bigint] IDENTITY(1,1) NOT NULL,
	[doctor_id] [bigint] NOT NULL,
	[date] [date] NOT NULL,
	[start_time] [time](7) NOT NULL,
	[end_time] [time](7) NOT NULL,
	[status] [varchar](10) NULL,
	[created_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[schedule_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[transactions]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[transactions](
	[transaction_id] [bigint] IDENTITY(1,1) NOT NULL,
	[account_id] [bigint] NOT NULL,
	[amount] [decimal](10, 2) NOT NULL,
	[payment_method] [varchar](10) NOT NULL,
	[transaction_type] [varchar](15) NOT NULL,
	[status] [varchar](10) NULL,
	[created_at] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[transaction_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[users]    Script Date: 5/19/2025 1:06:15 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[users](
	[user_id] [bigint] IDENTITY(1,1) NOT NULL,
	[account_id] [bigint] NOT NULL,
	[full_name] [nvarchar](255) NULL,
	[phone_number] [varchar](20) NULL,
	[address] [nvarchar](255) NULL,
	[created_at] [datetime] NULL,
	[updated_at] [datetime] NULL,
	[followup] [int] NULL,
	[note] [varchar](255) NULL,
 CONSTRAINT [PK__users__B9BE370F14CD2BA9] PRIMARY KEY CLUSTERED 
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET IDENTITY_INSERT [dbo].[accounts] ON 

INSERT [dbo].[accounts] ([account_id], [email], [password], [role], [created_at], [updated_at], [reset_password_token], [reset_token_expiry], [name_account], [provider]) VALUES (1, N'phongphuong22240@gmail.com', N'$2a$10$QRbLhtRPOvxHXv8Dc10LVu.O1mFkggV5MMC1fI5RJl.2OT/w93VWW', N'DOCTOR', CAST(N'2025-05-14T23:23:54.777' AS DateTime), CAST(N'2025-05-14T23:23:54.777' AS DateTime), NULL, NULL, N'doctor', NULL)
INSERT [dbo].[accounts] ([account_id], [email], [password], [role], [created_at], [updated_at], [reset_password_token], [reset_token_expiry], [name_account], [provider]) VALUES (2, N'phuongphuong22240@gmail.com', N'$2a$10$.3O/R7bh2.d802Y/luHZ7.9mg4zS3uglhBtnZrmO1nevKDmgUuJYG', N'PATIENT', CAST(N'2025-05-16T20:52:32.210' AS DateTime), CAST(N'2025-05-16T20:52:32.210' AS DateTime), NULL, NULL, N'user', NULL)
INSERT [dbo].[accounts] ([account_id], [email], [password], [role], [created_at], [updated_at], [reset_password_token], [reset_token_expiry], [name_account], [provider]) VALUES (3, N'fgdfjgkdfj@gmail.com', N'$2a$10$4ws5UwKbYkxJmLgT9ep2jekU3FauGFfFWZxAKhmX6Pak9m7FKvHK2', N'DOCTOR', CAST(N'2025-05-19T00:02:22.623' AS DateTime), CAST(N'2025-05-19T00:02:22.623' AS DateTime), NULL, NULL, N'doctor2', NULL)
INSERT [dbo].[accounts] ([account_id], [email], [password], [role], [created_at], [updated_at], [reset_password_token], [reset_token_expiry], [name_account], [provider]) VALUES (4, N'jskk@gmail.com', N'$2a$10$J8G6pn2k28Gg1IveQctoS.lK.AOw7DHjOnGVvgnT1tDVlW9DANtL2', N'DOCTOR', CAST(N'2025-05-19T00:03:21.887' AS DateTime), CAST(N'2025-05-19T00:03:21.887' AS DateTime), NULL, NULL, N'doctor3', NULL)
INSERT [dbo].[accounts] ([account_id], [email], [password], [role], [created_at], [updated_at], [reset_password_token], [reset_token_expiry], [name_account], [provider]) VALUES (5, N'sdkk@gmail.com', N'$2a$10$iqGZmlA3PxhaGL5X9yo2UebyJzyWJiAuBRZFhgms5CHtLUITqUEO2', N'DOCTOR', CAST(N'2025-05-19T00:04:32.250' AS DateTime), CAST(N'2025-05-19T00:04:32.250' AS DateTime), NULL, NULL, N'doctor4', NULL)
SET IDENTITY_INSERT [dbo].[accounts] OFF
GO
SET IDENTITY_INSERT [dbo].[appointments] ON 

INSERT [dbo].[appointments] ([appointment_id], [patient_id], [schedule_id], [booking_time], [status], [deposit_amount], [created_at], [updated_at], [period]) VALUES (1, 1, 1, CAST(N'2025-05-16T00:00:00.000' AS DateTime), N'CONFIRMED', CAST(500000.00 AS Decimal(10, 2)), CAST(N'2025-05-16T21:06:24.877' AS DateTime), CAST(N'2025-05-16T21:06:24.877' AS DateTime), 1)
INSERT [dbo].[appointments] ([appointment_id], [patient_id], [schedule_id], [booking_time], [status], [deposit_amount], [created_at], [updated_at], [period]) VALUES (2, 1, 5, CAST(N'2025-05-16T00:00:00.000' AS DateTime), N'PENDING', CAST(500000.00 AS Decimal(10, 2)), CAST(N'2025-05-16T21:07:02.910' AS DateTime), CAST(N'2025-05-16T21:07:02.910' AS DateTime), 2)
INSERT [dbo].[appointments] ([appointment_id], [patient_id], [schedule_id], [booking_time], [status], [deposit_amount], [created_at], [updated_at], [period]) VALUES (3, 1, 3, CAST(N'2025-05-16T00:00:00.000' AS DateTime), N'PENDING', CAST(500000.00 AS Decimal(10, 2)), CAST(N'2025-05-17T00:31:15.463' AS DateTime), CAST(N'2025-05-17T00:31:15.463' AS DateTime), 3)
INSERT [dbo].[appointments] ([appointment_id], [patient_id], [schedule_id], [booking_time], [status], [deposit_amount], [created_at], [updated_at], [period]) VALUES (4, 1, 7, CAST(N'2025-05-17T00:00:00.000' AS DateTime), N'CANCELLED', CAST(500000.00 AS Decimal(10, 2)), CAST(N'2025-05-17T00:31:39.290' AS DateTime), CAST(N'2025-05-17T00:31:39.290' AS DateTime), 3)
SET IDENTITY_INSERT [dbo].[appointments] OFF
GO
SET IDENTITY_INSERT [dbo].[doctors] ON 

INSERT [dbo].[doctors] ([doctor_id], [account_id], [specialty], [address], [license], [experience], [membership_id], [status], [created_at], [updated_at], [birthday], [fullname], [gender], [startdate], [enddate], [phone_number], [image]) VALUES (1, 1, N'Chuyên Viên Điều Dưỡng', N'Thành Phố Hồ Chí Minh', N'Tiến sĩ', N'3 năm', 1, N'PENDING', CAST(N'2025-05-14T23:26:30.540' AS DateTime), CAST(N'2025-05-14T23:26:30.540' AS DateTime), NULL, N'Nguyễn Hải Minh Phương', N'female', CAST(N'2025-05-17T00:00:00.000' AS DateTime), CAST(N'2025-06-17T00:00:00.000' AS DateTime), NULL, NULL)
INSERT [dbo].[doctors] ([doctor_id], [account_id], [specialty], [address], [license], [experience], [membership_id], [status], [created_at], [updated_at], [birthday], [fullname], [gender], [startdate], [enddate], [phone_number], [image]) VALUES (2, 2, N'Chuyên viên điều trị', N'Thành phố', N'Thạc sĩ', N'4 năm', 1, N'PENDING', CAST(N'2025-05-19T00:04:44.480' AS DateTime), CAST(N'2025-05-19T00:04:44.480' AS DateTime), NULL, N'Nguyễn', N'femail', NULL, NULL, NULL, NULL)
INSERT [dbo].[doctors] ([doctor_id], [account_id], [specialty], [address], [license], [experience], [membership_id], [status], [created_at], [updated_at], [birthday], [fullname], [gender], [startdate], [enddate], [phone_number], [image]) VALUES (3, 3, N'Chuyên viên', N'Thành phố', N'Thạc sĩ', N'2 năm', 1, N'PENDING', CAST(N'2025-05-19T00:05:16.623' AS DateTime), CAST(N'2025-05-19T00:05:16.623' AS DateTime), NULL, NULL, NULL, NULL, NULL, NULL, NULL)
INSERT [dbo].[doctors] ([doctor_id], [account_id], [specialty], [address], [license], [experience], [membership_id], [status], [created_at], [updated_at], [birthday], [fullname], [gender], [startdate], [enddate], [phone_number], [image]) VALUES (8, 4, N'Chuyên viên điều dưỡng', NULL, N'Giáo sư', N'2 năm', 1, N'PENDING', CAST(N'2025-05-19T00:06:18.957' AS DateTime), CAST(N'2025-05-19T00:06:18.957' AS DateTime), NULL, NULL, NULL, NULL, NULL, NULL, NULL)
SET IDENTITY_INSERT [dbo].[doctors] OFF
GO
SET IDENTITY_INSERT [dbo].[memberships] ON 

INSERT [dbo].[memberships] ([membership_id], [name], [price], [description], [created_at], [updated_at]) VALUES (1, N'Mức 1', CAST(20000.00 AS Decimal(10, 2)), N'Quản Lý Bình thường', CAST(N'2025-05-14T23:26:06.557' AS DateTime), CAST(N'2025-05-14T23:26:06.557' AS DateTime))
INSERT [dbo].[memberships] ([membership_id], [name], [price], [description], [created_at], [updated_at]) VALUES (2, N'Mức 2', CAST(50000.00 AS Decimal(10, 2)), N'Quản Lý Nâng Cao', CAST(N'2025-05-14T23:26:16.163' AS DateTime), CAST(N'2025-05-14T23:26:16.163' AS DateTime))
SET IDENTITY_INSERT [dbo].[memberships] OFF
GO
SET IDENTITY_INSERT [dbo].[order_items] ON 

INSERT [dbo].[order_items] ([order_item_id], [order_id], [product_id], [quantity], [price]) VALUES (1, 1, 1, 2, CAST(40000.00 AS Decimal(10, 2)))
INSERT [dbo].[order_items] ([order_item_id], [order_id], [product_id], [quantity], [price]) VALUES (2, 1, 2, 5, CAST(400000.00 AS Decimal(10, 2)))
INSERT [dbo].[order_items] ([order_item_id], [order_id], [product_id], [quantity], [price]) VALUES (3, 1, 3, 9, CAST(30000.00 AS Decimal(10, 2)))
INSERT [dbo].[order_items] ([order_item_id], [order_id], [product_id], [quantity], [price]) VALUES (4, 2, 1, 3, CAST(4000.00 AS Decimal(10, 2)))
INSERT [dbo].[order_items] ([order_item_id], [order_id], [product_id], [quantity], [price]) VALUES (5, 2, 4, 4, CAST(244444.00 AS Decimal(10, 2)))
INSERT [dbo].[order_items] ([order_item_id], [order_id], [product_id], [quantity], [price]) VALUES (6, 3, 2, 2, CAST(4232.00 AS Decimal(10, 2)))
INSERT [dbo].[order_items] ([order_item_id], [order_id], [product_id], [quantity], [price]) VALUES (7, 3, 3, 23, CAST(424.00 AS Decimal(10, 2)))
INSERT [dbo].[order_items] ([order_item_id], [order_id], [product_id], [quantity], [price]) VALUES (8, 4, 2, 3, CAST(423123.00 AS Decimal(10, 2)))
SET IDENTITY_INSERT [dbo].[order_items] OFF
GO
SET IDENTITY_INSERT [dbo].[order_membership] ON 

INSERT [dbo].[order_membership] ([id], [membership_id], [doctor_id], [count], [totalprice], [status], [date]) VALUES (2, 2, 1, 1, CAST(50000.00 AS Decimal(10, 2)), N'PENDING   ', CAST(N'2025-05-17T02:18:57.110' AS DateTime))
SET IDENTITY_INSERT [dbo].[order_membership] OFF
GO
SET IDENTITY_INSERT [dbo].[orders] ON 

INSERT [dbo].[orders] ([order_id], [patient_id], [total_amount], [order_date], [status], [created_at], [updated_at]) VALUES (1, 1, CAST(200000.00 AS Decimal(10, 2)), CAST(N'2025-05-16T00:00:00.000' AS DateTime), N'PENDING', CAST(N'2025-05-16T21:07:42.767' AS DateTime), CAST(N'2025-05-16T21:07:42.767' AS DateTime))
INSERT [dbo].[orders] ([order_id], [patient_id], [total_amount], [order_date], [status], [created_at], [updated_at]) VALUES (2, 1, CAST(300000.00 AS Decimal(10, 2)), CAST(N'2025-05-17T00:00:00.000' AS DateTime), N'PAID', CAST(N'2025-05-16T21:07:56.307' AS DateTime), CAST(N'2025-05-16T21:07:56.307' AS DateTime))
INSERT [dbo].[orders] ([order_id], [patient_id], [total_amount], [order_date], [status], [created_at], [updated_at]) VALUES (3, 1, CAST(30000000.00 AS Decimal(10, 2)), CAST(N'2025-05-17T00:00:00.000' AS DateTime), N'CANCELLED', CAST(N'2025-05-17T00:02:19.510' AS DateTime), CAST(N'2025-05-17T00:02:19.510' AS DateTime))
INSERT [dbo].[orders] ([order_id], [patient_id], [total_amount], [order_date], [status], [created_at], [updated_at]) VALUES (4, 1, CAST(30000.00 AS Decimal(10, 2)), CAST(N'2025-06-17T00:00:00.000' AS DateTime), N'PAID', CAST(N'2025-05-17T00:02:34.300' AS DateTime), CAST(N'2025-05-17T00:02:34.300' AS DateTime))
SET IDENTITY_INSERT [dbo].[orders] OFF
GO
SET IDENTITY_INSERT [dbo].[products] ON 

INSERT [dbo].[products] ([product_id], [doctor_id], [name], [price], [description], [created_at], [updated_at], [image], [count]) VALUES (1, 1, N'Thuốc nhỏ mắt đỏ', CAST(2000000.00 AS Decimal(10, 2)), N'thuốc nhỏ mắt', CAST(N'2025-05-15T00:11:35.377' AS DateTime), CAST(N'2025-05-15T00:11:35.377' AS DateTime), N'/uploads/105-thuoc-nho-mat-tri-dau-mat-do-4.png.webp', 20)
INSERT [dbo].[products] ([product_id], [doctor_id], [name], [price], [description], [created_at], [updated_at], [image], [count]) VALUES (2, 1, N'Thuốc nhỏ mắt', CAST(300000.00 AS Decimal(10, 2)), N'thuốc', CAST(N'2025-05-15T00:11:52.927' AS DateTime), CAST(N'2025-05-15T00:11:52.927' AS DateTime), N'/uploads/thuoc_nho_mat.jpg', 40)
INSERT [dbo].[products] ([product_id], [doctor_id], [name], [price], [description], [created_at], [updated_at], [image], [count]) VALUES (3, 1, N'Thuốc bổ não', CAST(30430.00 AS Decimal(10, 2)), N'thuốc bổ não', CAST(N'2025-05-15T00:12:10.103' AS DateTime), CAST(N'2025-05-15T00:12:10.103' AS DateTime), N'/uploads/bo-nao-cebraton-traphaco.jpg', 400)
INSERT [dbo].[products] ([product_id], [doctor_id], [name], [price], [description], [created_at], [updated_at], [image], [count]) VALUES (4, 1, N'panadol', CAST(10000.00 AS Decimal(10, 2)), N'Thuốc nhức đầu', CAST(N'2025-05-15T00:12:31.500' AS DateTime), CAST(N'2025-05-15T00:12:31.500' AS DateTime), N'/uploads/Panadol-Cam-Cum-1.webp', 400)
INSERT [dbo].[products] ([product_id], [doctor_id], [name], [price], [description], [created_at], [updated_at], [image], [count]) VALUES (5, 1, N'bisto', CAST(30000.00 AS Decimal(10, 2)), N'Thuốc chữa mệt ', CAST(N'2025-05-15T00:12:57.903' AS DateTime), CAST(N'2025-05-15T00:12:57.903' AS DateTime), N'/uploads/thuoc-biseptol-480.jpg', 500)
SET IDENTITY_INSERT [dbo].[products] OFF
GO
SET IDENTITY_INSERT [dbo].[schedules] ON 

INSERT [dbo].[schedules] ([schedule_id], [doctor_id], [date], [start_time], [end_time], [status], [created_at]) VALUES (1, 1, CAST(N'2025-05-16' AS Date), CAST(N'07:00:00' AS Time), CAST(N'08:30:00' AS Time), N'BOOKED', CAST(N'2025-05-16T21:03:04.293' AS DateTime))
INSERT [dbo].[schedules] ([schedule_id], [doctor_id], [date], [start_time], [end_time], [status], [created_at]) VALUES (3, 1, CAST(N'2025-05-16' AS Date), CAST(N'10:00:00' AS Time), CAST(N'11:30:00' AS Time), N'AVAILABLE', CAST(N'2025-05-16T21:03:41.727' AS DateTime))
INSERT [dbo].[schedules] ([schedule_id], [doctor_id], [date], [start_time], [end_time], [status], [created_at]) VALUES (4, 1, CAST(N'2025-05-16' AS Date), CAST(N'13:00:00' AS Time), CAST(N'14:30:00' AS Time), N'AVAILABLE', CAST(N'2025-05-16T21:04:00.350' AS DateTime))
INSERT [dbo].[schedules] ([schedule_id], [doctor_id], [date], [start_time], [end_time], [status], [created_at]) VALUES (5, 1, CAST(N'2025-05-17' AS Date), CAST(N'08:30:00' AS Time), CAST(N'10:00:00' AS Time), N'AVAILABLE', CAST(N'2025-05-16T21:04:24.090' AS DateTime))
INSERT [dbo].[schedules] ([schedule_id], [doctor_id], [date], [start_time], [end_time], [status], [created_at]) VALUES (6, 1, CAST(N'2025-05-18' AS Date), CAST(N'07:00:00' AS Time), CAST(N'08:30:00' AS Time), N'AVAILABLE', CAST(N'2025-05-17T00:29:55.487' AS DateTime))
INSERT [dbo].[schedules] ([schedule_id], [doctor_id], [date], [start_time], [end_time], [status], [created_at]) VALUES (7, 1, CAST(N'2025-05-17' AS Date), CAST(N'10:00:00' AS Time), CAST(N'11:30:00' AS Time), N'AVAILABLE', CAST(N'2025-05-17T00:30:13.937' AS DateTime))
SET IDENTITY_INSERT [dbo].[schedules] OFF
GO
SET IDENTITY_INSERT [dbo].[users] ON 

INSERT [dbo].[users] ([user_id], [account_id], [full_name], [phone_number], [address], [created_at], [updated_at], [followup], [note]) VALUES (1, 2, N'Nguyễn Hải Minh Phương', N'0219359213', N'Thành Phố Hồ Chí Minh', CAST(N'2025-05-16T20:52:32.400' AS DateTime), CAST(N'2025-05-16T20:52:32.400' AS DateTime), NULL, NULL)
SET IDENTITY_INSERT [dbo].[users] OFF
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__accounts__AB6E61640E5B8E48]    Script Date: 5/19/2025 1:06:15 AM ******/
ALTER TABLE [dbo].[accounts] ADD  CONSTRAINT [UQ__accounts__AB6E61640E5B8E48] UNIQUE NONCLUSTERED 
(
	[email] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [UQ__doctors__46A222CCEB557AB9]    Script Date: 5/19/2025 1:06:15 AM ******/
ALTER TABLE [dbo].[doctors] ADD  CONSTRAINT [UQ__doctors__46A222CCEB557AB9] UNIQUE NONCLUSTERED 
(
	[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__membersh__72E12F1B9203FD88]    Script Date: 5/19/2025 1:06:15 AM ******/
ALTER TABLE [dbo].[memberships] ADD  CONSTRAINT [UQ__membersh__72E12F1B9203FD88] UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__products__72E12F1B99B917BC]    Script Date: 5/19/2025 1:06:15 AM ******/
ALTER TABLE [dbo].[products] ADD  CONSTRAINT [UQ__products__72E12F1B99B917BC] UNIQUE NONCLUSTERED 
(
	[name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
/****** Object:  Index [UQ__users__46A222CC6909495F]    Script Date: 5/19/2025 1:06:15 AM ******/
ALTER TABLE [dbo].[users] ADD  CONSTRAINT [UQ__users__46A222CC6909495F] UNIQUE NONCLUSTERED 
(
	[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
/****** Object:  Index [UQ__users__A1936A6BD825B07E]    Script Date: 5/19/2025 1:06:15 AM ******/
ALTER TABLE [dbo].[users] ADD  CONSTRAINT [UQ__users__A1936A6BD825B07E] UNIQUE NONCLUSTERED 
(
	[phone_number] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[accounts] ADD  CONSTRAINT [DF__accounts__create__7D0E9093]  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[accounts] ADD  CONSTRAINT [DF__accounts__update__7E02B4CC]  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[appointments] ADD  DEFAULT (getdate()) FOR [booking_time]
GO
ALTER TABLE [dbo].[appointments] ADD  DEFAULT ('PENDING') FOR [status]
GO
ALTER TABLE [dbo].[appointments] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[appointments] ADD  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[carts] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[doctors] ADD  CONSTRAINT [DF__doctors__status__46E78A0C]  DEFAULT ('PENDING') FOR [status]
GO
ALTER TABLE [dbo].[doctors] ADD  CONSTRAINT [DF__doctors__created__47DBAE45]  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[doctors] ADD  CONSTRAINT [DF__doctors__updated__48CFD27E]  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[memberships] ADD  CONSTRAINT [DF__membershi__creat__49C3F6B7]  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[memberships] ADD  CONSTRAINT [DF__membershi__updat__4AB81AF0]  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[order_membership] ADD  CONSTRAINT [DF_order_membership_status]  DEFAULT ('PENDING') FOR [status]
GO
ALTER TABLE [dbo].[orders] ADD  DEFAULT (getdate()) FOR [order_date]
GO
ALTER TABLE [dbo].[orders] ADD  DEFAULT ('PENDING') FOR [status]
GO
ALTER TABLE [dbo].[orders] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[orders] ADD  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[products] ADD  CONSTRAINT [DF__products__create__4F7CD00D]  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[products] ADD  CONSTRAINT [DF__products__update__5070F446]  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[reviews] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[reviews] ADD  CONSTRAINT [DF_reviews_status]  DEFAULT ('PENDING') FOR [status]
GO
ALTER TABLE [dbo].[schedules] ADD  DEFAULT ('AVAILABLE') FOR [status]
GO
ALTER TABLE [dbo].[schedules] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[transactions] ADD  DEFAULT ('PENDING') FOR [status]
GO
ALTER TABLE [dbo].[transactions] ADD  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[users] ADD  CONSTRAINT [DF__users__created_a__5629CD9C]  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[users] ADD  CONSTRAINT [DF__users__updated_a__571DF1D5]  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[appointments]  WITH CHECK ADD  CONSTRAINT [FK__appointme__patie__5812160E] FOREIGN KEY([patient_id])
REFERENCES [dbo].[users] ([user_id])
GO
ALTER TABLE [dbo].[appointments] CHECK CONSTRAINT [FK__appointme__patie__5812160E]
GO
ALTER TABLE [dbo].[appointments]  WITH CHECK ADD FOREIGN KEY([schedule_id])
REFERENCES [dbo].[schedules] ([schedule_id])
GO
ALTER TABLE [dbo].[carts]  WITH CHECK ADD  CONSTRAINT [FK__carts__patient_i__59FA5E80] FOREIGN KEY([patient_id])
REFERENCES [dbo].[users] ([user_id])
GO
ALTER TABLE [dbo].[carts] CHECK CONSTRAINT [FK__carts__patient_i__59FA5E80]
GO
ALTER TABLE [dbo].[carts]  WITH CHECK ADD  CONSTRAINT [FK__carts__product_i__5AEE82B9] FOREIGN KEY([product_id])
REFERENCES [dbo].[products] ([product_id])
GO
ALTER TABLE [dbo].[carts] CHECK CONSTRAINT [FK__carts__product_i__5AEE82B9]
GO
ALTER TABLE [dbo].[doctors]  WITH CHECK ADD  CONSTRAINT [FK_doctors_memberships] FOREIGN KEY([membership_id])
REFERENCES [dbo].[memberships] ([membership_id])
GO
ALTER TABLE [dbo].[doctors] CHECK CONSTRAINT [FK_doctors_memberships]
GO
ALTER TABLE [dbo].[order_items]  WITH CHECK ADD FOREIGN KEY([order_id])
REFERENCES [dbo].[orders] ([order_id])
GO
ALTER TABLE [dbo].[order_items]  WITH CHECK ADD  CONSTRAINT [FK__order_ite__produ__5EBF139D] FOREIGN KEY([product_id])
REFERENCES [dbo].[products] ([product_id])
GO
ALTER TABLE [dbo].[order_items] CHECK CONSTRAINT [FK__order_ite__produ__5EBF139D]
GO
ALTER TABLE [dbo].[order_membership]  WITH CHECK ADD  CONSTRAINT [FK_order_membership_doctors] FOREIGN KEY([doctor_id])
REFERENCES [dbo].[doctors] ([doctor_id])
GO
ALTER TABLE [dbo].[order_membership] CHECK CONSTRAINT [FK_order_membership_doctors]
GO
ALTER TABLE [dbo].[order_membership]  WITH CHECK ADD  CONSTRAINT [FK_order_membership_memberships] FOREIGN KEY([membership_id])
REFERENCES [dbo].[memberships] ([membership_id])
GO
ALTER TABLE [dbo].[order_membership] CHECK CONSTRAINT [FK_order_membership_memberships]
GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD  CONSTRAINT [FK__orders__patient___5FB337D6] FOREIGN KEY([patient_id])
REFERENCES [dbo].[users] ([user_id])
GO
ALTER TABLE [dbo].[orders] CHECK CONSTRAINT [FK__orders__patient___5FB337D6]
GO
ALTER TABLE [dbo].[products]  WITH CHECK ADD  CONSTRAINT [FK__products__doctor__60A75C0F] FOREIGN KEY([doctor_id])
REFERENCES [dbo].[doctors] ([doctor_id])
GO
ALTER TABLE [dbo].[products] CHECK CONSTRAINT [FK__products__doctor__60A75C0F]
GO
ALTER TABLE [dbo].[reviews]  WITH CHECK ADD  CONSTRAINT [FK__reviews__doctor___619B8048] FOREIGN KEY([doctor_id])
REFERENCES [dbo].[doctors] ([doctor_id])
GO
ALTER TABLE [dbo].[reviews] CHECK CONSTRAINT [FK__reviews__doctor___619B8048]
GO
ALTER TABLE [dbo].[reviews]  WITH CHECK ADD  CONSTRAINT [FK__reviews__patient__628FA481] FOREIGN KEY([patient_id])
REFERENCES [dbo].[users] ([user_id])
GO
ALTER TABLE [dbo].[reviews] CHECK CONSTRAINT [FK__reviews__patient__628FA481]
GO
ALTER TABLE [dbo].[schedules]  WITH CHECK ADD  CONSTRAINT [FK__schedules__docto__6383C8BA] FOREIGN KEY([doctor_id])
REFERENCES [dbo].[doctors] ([doctor_id])
GO
ALTER TABLE [dbo].[schedules] CHECK CONSTRAINT [FK__schedules__docto__6383C8BA]
GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD  CONSTRAINT [FK__transacti__accou__40C49C62] FOREIGN KEY([account_id])
REFERENCES [dbo].[accounts] ([account_id])
GO
ALTER TABLE [dbo].[transactions] CHECK CONSTRAINT [FK__transacti__accou__40C49C62]
GO
ALTER TABLE [dbo].[users]  WITH CHECK ADD  CONSTRAINT [FK__users__account_i__04AFB25B] FOREIGN KEY([account_id])
REFERENCES [dbo].[accounts] ([account_id])
GO
ALTER TABLE [dbo].[users] CHECK CONSTRAINT [FK__users__account_i__04AFB25B]
GO
ALTER TABLE [dbo].[accounts]  WITH CHECK ADD  CONSTRAINT [CK__accounts__role__7C1A6C5A] CHECK  (([role]='ADMIN' OR [role]='DOCTOR' OR [role]='PATIENT'))
GO
ALTER TABLE [dbo].[accounts] CHECK CONSTRAINT [CK__accounts__role__7C1A6C5A]
GO
ALTER TABLE [dbo].[appointments]  WITH CHECK ADD CHECK  (([deposit_amount]>=(0)))
GO
ALTER TABLE [dbo].[appointments]  WITH CHECK ADD CHECK  (([status]='CANCELLED' OR [status]='CONFIRMED' OR [status]='PENDING'))
GO
ALTER TABLE [dbo].[carts]  WITH CHECK ADD CHECK  (([quantity]>(0)))
GO
ALTER TABLE [dbo].[doctors]  WITH CHECK ADD  CONSTRAINT [CK__doctors__status__6A30C649] CHECK  (([status]='REJECTED' OR [status]='APPROVED' OR [status]='PENDING'))
GO
ALTER TABLE [dbo].[doctors] CHECK CONSTRAINT [CK__doctors__status__6A30C649]
GO
ALTER TABLE [dbo].[memberships]  WITH CHECK ADD  CONSTRAINT [CK__membershi__price__6B24EA82] CHECK  (([price]>=(0)))
GO
ALTER TABLE [dbo].[memberships] CHECK CONSTRAINT [CK__membershi__price__6B24EA82]
GO
ALTER TABLE [dbo].[order_items]  WITH CHECK ADD CHECK  (([price]>=(0)))
GO
ALTER TABLE [dbo].[order_items]  WITH CHECK ADD CHECK  (([quantity]>(0)))
GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD CHECK  (([status]='CANCELLED' OR [status]='SHIPPED' OR [status]='PAID' OR [status]='PENDING'))
GO
ALTER TABLE [dbo].[orders]  WITH CHECK ADD CHECK  (([total_amount]>=(0)))
GO
ALTER TABLE [dbo].[products]  WITH CHECK ADD  CONSTRAINT [CK__products__price__6FE99F9F] CHECK  (([price]>=(0)))
GO
ALTER TABLE [dbo].[products] CHECK CONSTRAINT [CK__products__price__6FE99F9F]
GO
ALTER TABLE [dbo].[reviews]  WITH CHECK ADD CHECK  (([rating]>=(1) AND [rating]<=(5)))
GO
ALTER TABLE [dbo].[schedules]  WITH CHECK ADD CHECK  (([status]='BOOKED' OR [status]='AVAILABLE'))
GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD CHECK  (([amount]>=(0)))
GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD CHECK  (([payment_method]='VNPAY' OR [payment_method]='MOMO'))
GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD CHECK  (([status]='FAILED' OR [status]='SUCCESS' OR [status]='PENDING'))
GO
ALTER TABLE [dbo].[transactions]  WITH CHECK ADD CHECK  (([transaction_type]='MEMBERSHIP' OR [transaction_type]='ORDER' OR [transaction_type]='APPOINTMENT'))
GO
USE [master]
GO
ALTER DATABASE [temp_clinic_booking1] SET  READ_WRITE 
GO
