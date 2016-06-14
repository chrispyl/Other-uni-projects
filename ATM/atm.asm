MVI A, 0000H
OUT 20H 			 ;initialize wrong PIN lamp to off
MVI A, 0000H		         ;initialize the lamp, which shows if withrawal money and asked types don't much, to off
OUT 21H

				 ;here 2 inputs are needed because the codes have 4 digits
IN 00H				 ;first byte of card code
STA 0100H	
IN 01H				 ;second byte of card code
STA 0101H
IN 02H				 ;first byte of use PIN
STA 0102H
IN 03H				 ;second byte of use PIN
STA 0103H

LHLD 0100H			 ;put the card code to DE and the user PIN to HL and compare them
XCHG 
LHLD 0102H

MOV A,H
CMP D
JNZ WRONG_CODE 		 	 ;if they don't match turn on the lamp 
MOV A,E
CMP L
JNZ WRONG_CODE

IN 04H				 ;the user enters the withdrawal amount per byte 
STA 0104H
IN 05H;
STA 0105H

IN 06 				 ;the user chooses if he wants to pick the paer money types (if yes, input is considered 1)
MVI B, 0001H
CMP B
JZ CHOOSE_CASH 		 	 ;if 1 jump to selection

				 ;if it reached here, the user chose to not pick types of paper money
				 ;in order to find how many paper money the user will take from each type i start divisions until the remnant becomes negative
MVI A, 0064H 		 	 ;put 100 to A and then to memory in order to subtract it in every iteration from the withdrawal amount
STA 0106H 
LHLD 0104H			 ;load HL with the withdrawal amount
SHLD 0202H			 ;the result of the subtraction will be saved to positions 0202H-0203H in memory
MVI A, 0000H
STA 0300H 			 ;initialize position 0300H which will hold how many 100e the user will take


EKATO: nop
LHLD 0202H           		 ;take the amount which i want to break into 100e
XCHG                 		 ;put it into DE
LHLD 0106H           		 ;put 100 to HL
MOV A, E       		 	 ;put to A the 8 LSBs of DE
SUB L            	 	 ;subtract the corresponding of HL from the ones of Α
MOV L, A             		 ;save the result to L
MOV A, D             		 ;do the same procedure for the 8 MSBs, but also taking into account the carry from the previous subtraction
SBB H                       
MOV H, A             		 ;move the result to H and then refresh the values of the positions 0202H-0203H in memory
SHLD 0202H              

LDA 0300H			 ;load and increment the quotient and then refresh the value of the memory
INR A
STA 0300H 

MOV A,H				 ;compare the most significant byte of the number resulted from the subtraction with 0
MVI H, 0000H
JZ CHECK_SECOND 	 	 ;if it is zero then compare the second byte
JC NEXT1			 ;if the carry flag is up, it means that an underflow has happened and it can continue to the calculation of the 50euros
JP EKATO			 ;if the number is postitive it means that the division can continue
JMP NEXT1			 ;if none of the above is true, it continues to the calculation of 50euros 

CHECK_SECOND: nop
MOV A, L			 ;the least significant bytes are checked
MVI L, 0000H
CMP L				 ;the checks are the same tith the ones of the most significant bytes
JC NEXT1
JP EKATO
JMP NEXT1
 

NEXT1: nop
LDA 0300H			 ;load the memory position where the quotient lies and subtract 1 because the iteration where the result got negative was taken into account	
DCR A	
STA 0300H
LDA 0202H			 ;load A with the negative result and add 100 in order to take the remnant of the division, only A is needed as the remnant is surely <2^8
ADI 0064H	
STA 0202H

MOV A, E			 ;the divisions continue here wiith the same logic but for 8 bit, A will hold from now on the remnant
MVI B, 0032H		 	 ;B will hold from now on the number subtracted every time (50,20,10)
MVI E, 0000H		 	 ;E will hold frοm now on the quotients

PENHNTA: nop
SUB B	
INR E
CPI 0000H	
JP PENHNTA
DCR E

ADI 0032H
MOV D,A				 ;save A to D in order not to lose the value while i put E into memory

MOV A,E
STA 0301H			 ;position 0301H has how many 50e the user will take

MOV A,D
MVI B, 0014H
MVI E, 0000H
TWENTY: nop
SUB B
INR E
CPI 0000H
JP TWENTY
DCR E

ADI 0014H
MOV D,A

MOV A,E
STA 0302H			 ;position 0302H the 20e

MOV A,D
MVI B, 000AH
MVI E, 0000H
DEKA: nop
SUB B
INR E
CPI 0000H
JP DEKA
DCR E

MOV A,E
STA 0303H			 ;position 0303H the 10e

				 ;toggle each switch the corresponding amount of times
LDA 0300H
OUT 10H				 ;100
LDA 0301H
OUT 11H				 ;50
LDA 0302H
OUT 12H				 ;20
LDA 0303H
OUT 13H				 ;10
HLT

CHOOSE_CASH: nop

				 ;after the user decides how many paer moeny he wants from each type, a verification is done to check if they add up to the withdrawal amount, i do this by adding the ones he asks and compare them with the withdrawal amount
MVI H, 0000H		 	 ;initialize HL, DE
MVI L, 0000H
MVI D, 0000H
MVI E, 0000H
MVI A, 0000H
STA 0260H  			 ;initialize position 0260H which will have the total of those the user ask for
STA 0261H

IN 07H 				 ;takes how many 100e the user asks
STA 0300H 
CPI 0000H			 ;if he doesn't askgo to 50e
JZ JUMP100

LHLD 0260H			 ;load HL with the total
MVI C, 0000H		 	 ;C counts when to stop the additions, which will happen when C becomes equal to the amount of paper money of this type asked by the user
MVI D, 0000H
MVI E, 0064H		 	 ;DE will be used as a pair because the result may be greater than 2^8 and it has the amount which is added depending on the paper money type
SUM100: nop
DAD D 
SHLD 0260H;
INR C
CMP C
JNZ SUM100

				 ;the same procedure is done for all the paper money types


JUMP100: nop
IN 08H				 ;takes how many 50e
STA 0301H
CPI 0000H
JZ JUMP50

MVI C, 0000H
MVI E, 0032H
SUM50: nop
DAD D 
SHLD 0260H
INR C
CMP C
JNZ SUM50




JUMP50: nop
IN 09H				 ;takes how many 20e
STA 0302H
CPI 0000H
JZ JUMP20

MVI C, 0000H
MVI E, 0014H
SUM20: nop
DAD D
SHLD 0260H
INR C
CMP C
JNZ SUM20




JUMP20: nop
IN 0AH				 ;takes how many 10e
STA 0303H
CPI 0000H
JZ JUMP10

MVI C, 0000H
MVI E, 000AH
SUM10: nop
DAD D
SHLD 0260H
INR C
CMP C
JNZ SUM10

JUMP10: nop
LHLD 0104H			 ;load HL with the withdrawal amount
XCHG				 ;put it to DE in order to do the comparison next
LHLD 0260H			 ;load the total of the paper money types he asks
MOV A,H
CMP D				 ;comparison of the 2 bytes, if they don't match the lamp at the output 21 is turned on
JNZ WRONG_SUM
MOV A,L
CMP E
JNZ WRONG_SUM
				 ;if the amounts match, toggle each switch the corresponding amount of times
LDA 0300H
OUT 10H				 ;switch 100
LDA 0301H
OUT 11H 			 ;switch 50
LDA 0302H
OUT 12H 			 ;switch 20
LDA 0303H
OUT 13H 			 ;switch 10
HLT

WRONG_SUM: nop
MVI  A, 0001H 		 	 ;turn on the wrong total amount of paper money types lamp
OUT 21H
HLT

WRONG_CODE: nop
MVI A, 0001H 		 	 ;turn on the wrong PIN lamp
OUT 20H
HLT
