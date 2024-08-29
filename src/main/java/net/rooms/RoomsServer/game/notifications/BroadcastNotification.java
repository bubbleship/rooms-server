package net.rooms.RoomsServer.game.notifications;

public record BroadcastNotification(
		String payload // Json payload containing the game packet that needs transfer
) {
}
