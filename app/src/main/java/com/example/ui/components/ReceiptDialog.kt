package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ParkingCalculator
import com.example.model.ParkingTicket
import com.example.model.TicketStatus
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.MpesaGreenDark
import com.example.ui.theme.ParkingBlue
import com.example.ui.theme.PlateDark
import com.example.ui.theme.PlateYellow
import com.example.ui.theme.ReceiptBg
import com.example.ui.theme.ReceiptBorder
import com.example.ui.theme.ReceiptText
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun ReceiptDialog(
    ticket: ParkingTicket,
    onDismiss: () -> Unit,
    onMarkExited: (ticketId: String) -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .testTag("parking_receipt_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Modal Top Close Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Parking Receipt",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("receipt_close_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Printable Physical-style Receipt Paper
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ReceiptBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ReceiptBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MpesaGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "P",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "PARKGO SERVICES",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = ReceiptText,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Automated Parking Systems • Nairobi Hub",
                            fontSize = 11.sp,
                            color = Slate600
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Status pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFFDCFCE7),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MpesaGreenDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "M-PESA PAYMENT CONFIRMED",
                                    color = MpesaGreenDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dashed divider line
                        DashedDivider()

                        Spacer(modifier = Modifier.height(14.dp))

                        // Key-Value receipt items
                        ReceiptRow(label = "RECEIPT NO.", value = ticket.mpesaReceiptNo ?: "RJK48X9L2Q", isMono = true)
                        ReceiptRow(label = "CAR REG NO.", value = ticket.regNumber, isPlate = true)
                        ReceiptRow(label = "VEHICLE MODEL", value = ticket.carModel)
                        ReceiptRow(label = "PARKING BAY", value = ticket.parkingBay)
                        ReceiptRow(label = "ENTRY TIME", value = ticket.formattedEntryTime)
                        ReceiptRow(label = "PAYMENT TIME", value = ticket.formattedPaidTime)
                        ReceiptRow(label = "PARKED DURATION", value = ticket.formattedDuration, isBold = true)
                        ReceiptRow(
                            label = "PAID VIA",
                            value = "M-PESA (${ticket.mpesaPhone ?: "07XX XXX XXX"})"
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Amount Due & Paid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "TOTAL PAID",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = ReceiptText
                                )
                                Text(
                                    text = ParkingCalculator.getRateExplanation(ticket.durationMinutes),
                                    fontSize = 10.sp,
                                    color = Slate600
                                )
                            }

                            Text(
                                text = "Ksh ${ticket.rateKsh}.00",
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = MpesaGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        // Barcode representation
                        SimulatedBarcode(code = ticket.mpesaReceiptNo ?: ticket.id.take(10))

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "* SCAN AT EXIT BARRIER *",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Slate600
                        )
                        Text(
                            text = "Valid for 15 minutes exit window after payment.",
                            fontSize = 10.sp,
                            color = Slate600,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Receipt copied & saved to downloads!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_receipt_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", fontSize = 13.sp)
                    }

                    if (ticket.status == TicketStatus.PAID) {
                        Button(
                            onClick = {
                                onMarkExited(ticket.id)
                                Toast.makeText(context, "Car ${ticket.regNumber} exited. Bay released!", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("exit_bay_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = ParkingBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Exit Bay", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isMono: Boolean = false,
    isPlate: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate600
        )

        if (isPlate) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = PlateYellow,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCA8A04))
            ) {
                Text(
                    text = value,
                    color = PlateDark,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        } else {
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
                fontFamily = if (isMono) FontFamily.Monospace else FontFamily.SansSerif,
                color = ReceiptText
            )
        }
    }
}

@Composable
private fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        drawLine(
            color = Color(0xFFCBD5E1),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
            strokeWidth = 2f
        )
    }
}

@Composable
private fun SimulatedBarcode(code: String) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(44.dp)
    ) {
        val totalWidth = size.width
        val barCount = 42
        val barWidth = totalWidth / (barCount * 1.5f)

        // Seeded sequence based on code string
        val codeHash = code.hashCode()
        for (i in 0 until barCount) {
            val isThick = ((codeHash shr (i % 30)) and 1) == 1
            val w = if (isThick) barWidth * 1.6f else barWidth * 0.8f
            val x = i * (totalWidth / barCount)
            drawRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(x, 0f),
                size = Size(w.coerceAtMost(barWidth * 1.8f), size.height)
            )
        }
    }
}
